-- Ejecutar una sola vez en MySQL sobre gestion_citas.
-- Normaliza roles antiguos a los tres roles del sistema.

USE gestion_citas;

UPDATE usuario SET rol = 'ADMIN' WHERE UPPER(rol) IN ('ROLE_ADMIN', 'ADMIN');
UPDATE usuario SET rol = 'MEDICO' WHERE UPPER(rol) IN ('ROLE_MEDICO', 'MEDICO', 'MÉDICO');
UPDATE usuario SET rol = 'PACIENTE' WHERE UPPER(rol) IN ('ROLE_USER', 'USER', 'ROLE_PACIENTE', 'PACIENTE');

-- Verificación
SELECT id, nombre, correo, rol FROM usuario ORDER BY id;
