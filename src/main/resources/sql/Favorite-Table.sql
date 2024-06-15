ALTER USER cheminfra quota unlimited on act0_tab;
ALTER USER cheminfra quota unlimited on act0_idx;

DEFINE tab_space_table = act0_tab
DEFINE tab_space_index = act0_idx
PROMPT Creating TABLE favorite_instrument

CREATE TABLE cheminfra.favorite_instrument (
    id              NUMBER(8, 0) NOT NULL
        CONSTRAINT pk_favorite_instrument PRIMARY KEY
            USING INDEX TABLESPACE &tab_space_index
                STORAGE ( INITIAL 10k ),
    user_name       VARCHAR2(32) NOT NULL,
    cre_date        DATE NOT NULL,
    is_active       NUMBER(1, 0) NOT NULL,    
    instrument_id   NUMBER(8, 0) NOT NULL
        CONSTRAINT fk_instrument_id
            REFERENCES cheminfra.instrument ( id )
)
TABLESPACE &tab_space_table
    STORAGE ( INITIAL 200K );
    

grant select, insert, update, delete on cheminfra.favorite_instrument to CHEMINFRAUSER;

drop SEQUENCE FAV_SEQ;
CREATE SEQUENCE CHEMINFRA.FAV_SEQ MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
grant select on CHEMINFRA.FAV_SEQ to CHEMINFRAUSER;

insert into CHEMINFRA.favorite_instrument (id, instrument_id, user_name, cre_date, is_active) values (FAV_SEQ.nextval, 960, 'BENMEKA1', '21/01/21', 1);

--select count(*) from all_sequences where sequence_name = 'FAV_SEQ';
--select FAV_SEQ.nextval from dual;