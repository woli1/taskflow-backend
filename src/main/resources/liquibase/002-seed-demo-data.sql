--liquibase formatted sql

--changeset walid:100-seed-demo-user context:demo
INSERT INTO users (name, email, password, created_at)
VALUES ('Demo User', 'demo@taskflow.dev',
        '$2b$10$P16gaNfBUH9/.8SlTswfleJ11k3PsidJ/IkQf/cRloJs4iB0kvT6i',
        CURRENT_TIMESTAMP);

--changeset walid:101-seed-demo-projects context:demo
INSERT INTO projects (name, description, owner_id, created_at) VALUES
                                                                   ('Lancement du site web', 'Refonte et mise en ligne du site vitrine',
                                                                    (SELECT id FROM users WHERE email = 'demo@taskflow.dev'), CURRENT_TIMESTAMP),
                                                                   ('Préparation de l''examen', 'Plan de révision sur 3 semaines',
                                                                    (SELECT id FROM users WHERE email = 'demo@taskflow.dev'), CURRENT_TIMESTAMP);

--changeset walid:102-seed-demo-tasks-website context:demo
INSERT INTO tasks (title, description, status, priority, due_date, project_id, created_at) VALUES
                                                                                               ('Maquettes Figma', 'Valider les écrans principaux avec le client', 'DONE', 'HIGH', DATE '2026-10-05',
                                                                                                (SELECT id FROM projects WHERE name = 'Lancement du site web'), CURRENT_TIMESTAMP),
                                                                                               ('Développer la page d''accueil', 'Intégration Angular + Material', 'IN_PROGRESS', 'HIGH', DATE '2026-10-20',
                                                                                                (SELECT id FROM projects WHERE name = 'Lancement du site web'), CURRENT_TIMESTAMP),
                                                                                               ('Configurer le nom de domaine', NULL, 'TODO', 'MEDIUM', DATE '2026-11-01',
                                                                                                (SELECT id FROM projects WHERE name = 'Lancement du site web'), CURRENT_TIMESTAMP),
                                                                                               ('Rédiger les mentions légales', NULL, 'TODO', 'LOW', NULL,
                                                                                                (SELECT id FROM projects WHERE name = 'Lancement du site web'), CURRENT_TIMESTAMP);

--changeset walid:103-seed-demo-tasks-exam context:demo
INSERT INTO tasks (title, description, status, priority, due_date, project_id, created_at) VALUES
                                                                                               ('Réviser les chapitres 1 à 3', NULL, 'DONE', 'MEDIUM', DATE '2026-10-08',
                                                                                                (SELECT id FROM projects WHERE name = 'Préparation de l''examen'), CURRENT_TIMESTAMP),
                                                                                               ('Faire les annales', 'Au moins 3 sujets complets chronométrés', 'IN_PROGRESS', 'HIGH', DATE '2026-10-25',
                                                                                                (SELECT id FROM projects WHERE name = 'Préparation de l''examen'), CURRENT_TIMESTAMP),
                                                                                               ('Fiche de synthèse', NULL, 'TODO', 'MEDIUM', DATE '2026-10-30',
                                                                                                (SELECT id FROM projects WHERE name = 'Préparation de l''examen'), CURRENT_TIMESTAMP);