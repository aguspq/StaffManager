CREATE TABLE dept (
    deptno SERIAL PRIMARY KEY,
    dname VARCHAR(255),
    loc VARCHAR(255),
    isactive BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE employee (
    empno SERIAL PRIMARY KEY,
    ename VARCHAR(255),
    job VARCHAR(255),
    deptno INTEGER,
    isactive BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_employee_dept FOREIGN KEY (deptno) REFERENCES dept(deptno)
);

CREATE TABLE project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    isactive BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE employee_project (
    employee_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    PRIMARY KEY (employee_id, project_id),
    CONSTRAINT fk_emp_proj_employee FOREIGN KEY (employee_id) REFERENCES employee(empno),
    CONSTRAINT fk_emp_proj_project FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_no_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_no_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credential_no_expired BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
