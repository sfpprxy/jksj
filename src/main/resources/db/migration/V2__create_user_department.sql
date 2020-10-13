create table app_user
(
    id              text   not null PRIMARY KEY,
    password        text   not null,
    role_ids        text[] not null,
    oa_id           text,
    oa_delete_flag  int,
    oa_login_name   text,
    oa_real_name    text,
    oa_dept_id      text,
    oa_password     text,
    oa_status       int,
    oa_sync_time    text,
    oa_jky_flag     int,
    oa_finance_flag int,
    oa_id_card      text,
    oa_phone        text,
    vh_id           int,
    vh_user_name    text,
    vh_pwd          text,
    vh_dept_id      int,
    vh_enable       int,
    vh_spec_id      int
);

create table department
(
    id             text not null PRIMARY KEY,
    oa_id          text,
    oa_dept_name   text,
    oa_dept_code   text,
    oa_delete_flag int,
    oa_parent_id   text,
    oa_state       text,
    oa_is_company  int,
    oa_sync_time   text,
    vh_dept_id     int,
    vh_dept_name   text,
    vh_category    int,
    vh_manager_id  int,
    vh_parent_id   int,
    vh_enabled     int,
    vh_unit_code   text
);
