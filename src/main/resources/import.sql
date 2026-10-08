-- Insertar 10 productos de prueba
INSERT INTO product (id, code, name, description) VALUES (1, 'PRD-001', 'Laptop Pro 15', 'Laptop de alto rendimiento con 16GB RAM y 512GB SSD');
INSERT INTO product (id, code, name, description) VALUES (2, 'PRD-002', 'Teclado Mecánico', 'Teclado mecánico RGB con switches Blue');
INSERT INTO product (id, code, name, description) VALUES (3, 'PRD-003', 'Mouse Inalámbrico', 'Mouse ergonómico óptico de alta precisión');
INSERT INTO product (id, code, name, description) VALUES (4, 'PRD-004', 'Monitor 27 IPS', 'Monitor Ultra HD 4K de 27 pulgadas a 144Hz');
INSERT INTO product (id, code, name, description) VALUES (5, 'PRD-005', 'Audífonos Noise Cancelling', 'Audífonos bluetooth con cancelación activa de ruido');
INSERT INTO product (id, code, name, description) VALUES (6, 'PRD-006', 'Silla Ergonómica', 'Silla de oficina con soporte lumbar ajustable');
INSERT INTO product (id, code, name, description) VALUES (7, 'PRD-007', 'Disco Duro Externo 2TB', 'Almacenamiento portátil USB 3.0 ultra rápido');
INSERT INTO product (id, code, name, description) VALUES (8, 'PRD-008', 'Cámara Web Full HD', 'Webcam 1080p con micrófono estéreo integrado');
INSERT INTO product (id, code, name, description) VALUES (9, 'PRD-009', 'Micrófono Condensador', 'Micrófono USB ideal para streaming y podcasting');
INSERT INTO product (id, code, name, description) VALUES (10, 'PRD-010', 'Escritorio Elevable', 'Escritorio con ajuste de altura eléctrico');

-- Reiniciar la secuencia de IDs para que los nuevos insert vía POST inicien desde el ID 11
ALTER TABLE product ALTER COLUMN id RESTART WITH 11;