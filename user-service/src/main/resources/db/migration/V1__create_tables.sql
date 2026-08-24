CREATE TABLE users (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email      VARCHAR(255) UNIQUE NOT NULL,
    username  VARCHAR(255),
    password   VARCHAR(255) NOT NULL,
    version    BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP
);

CREATE TABLE contacts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255),
    phone       VARCHAR(50),
    telegram_id VARCHAR(100),
    created_at  TIMESTAMP DEFAULT now(),
    updated_at  TIMESTAMP
);

CREATE TABLE channels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(255) UNIQUE NOT NULL,
);

CREATE TABLE contacts_channels (
    contact_id UUID NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    channel_id UUID NOT NULL REFERENCES channels(id) ON DELETE CASCADE,
    address VARCHAR(255) NOT NULL,
    PRIMARY KEY (contact_id, channel_id)
);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
    BEGIN
    NEW.updated_at := now();
    RETURN NEW;
    END;
$$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION prevent_duplicate_contact_channel()
RETURNS TRIGGER AS $$
    DECLARE
         owner UUID;
         address_twins BIGINT;
    BEGIN
    SELECT owner_id INTO STRICT owner
    FROM contacts
    WHERE id = NEW.contact_id;

    SELECT count(*) INTO address_twins FROM contacts_channels AS cc
    JOIN contacts ON cc.contact_id = contacts.id
    WHERE cc.address = NEW.address AND owner_id = owner;

    IF address_twins > 0 THEN
       RAISE EXCEPTION 'contacts of owner with id % must be with unique channel addresses', owner;
    END;
$$
LANGUAGE plpgsql;


CREATE TRIGGER before_insert_contacts_channels
BEFORE INSERT OR UPDATE ON contacts_channels
FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_contact_channel();

CREATE TRIGGER before_update_users
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER before_update_contacts
BEFORE UPDATE ON contacts
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE UNIQUE INDEX contacts_owner_email_key ON contacts(owner_id, email);