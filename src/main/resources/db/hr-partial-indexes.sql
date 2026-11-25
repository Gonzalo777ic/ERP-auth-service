-- Índices parciales RRHH (maestros soft-delete).
-- Ejecutar tras crear tablas (ddl-auto o migración). Libros de contrato/ausencia NO se ocultan.

CREATE UNIQUE INDEX IF NOT EXISTS ux_hr_departments_company_code_active
    ON hr_departments (company_id, lower(code))
    WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_hr_positions_company_code_active
    ON hr_positions (company_id, lower(code))
    WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_hr_employees_company_code_active
    ON hr_employees (company_id, lower(code))
    WHERE deleted_at IS NULL;

-- Documento (tipo + número) es la clave de negocio del trabajador vigente.
-- company_id guarda el id de la entidad legal empleadora.
CREATE UNIQUE INDEX IF NOT EXISTS ux_hr_employees_document_active
    ON hr_employees (company_id, document_type, document_number)
    WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_hr_employees_cuspp_active
    ON hr_employees (company_id, cuspp)
    WHERE deleted_at IS NULL AND cuspp IS NOT NULL;

-- Departamento, puesto y fecha de ingreso dejan de ser obligatorios en la ficha personal.
ALTER TABLE hr_employees ALTER COLUMN department_id DROP NOT NULL;
ALTER TABLE hr_employees ALTER COLUMN position_id DROP NOT NULL;
ALTER TABLE hr_employees ALTER COLUMN hire_date DROP NOT NULL;

-- Un User de identidad como máximo en una ficha activa
CREATE UNIQUE INDEX IF NOT EXISTS ux_hr_employees_user_active
    ON hr_employees (user_id)
    WHERE deleted_at IS NULL AND user_id IS NOT NULL;

-- hr_employment_contracts y hr_absences son libros: sin soft-delete ni índices de ocultamiento.
