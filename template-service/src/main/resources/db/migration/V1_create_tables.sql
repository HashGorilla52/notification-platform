CREATE TABLE templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    owner_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP
);

CREATE UNIQUE INDEX templates_owner_id_name_key ON templates(name, owner_id);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
    BEGIN
    NEW.updated_at := now();
    RETURN NEW;
    END;
    $$
LANGUAGE plpgsql;

CREATE TRIGGER before_update_templates
BEFORE UPDATE ON templates
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE template_placeholders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    template_id UUID NOT NULL REFERENCES templates(id) ON DELETE CASCADE
);



