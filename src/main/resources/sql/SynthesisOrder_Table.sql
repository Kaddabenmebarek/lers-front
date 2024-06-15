--ALTER TABLE cheminfra.synthesis_library_order DROP CONSTRAINT fk_file_attached_id;
--ALTER TABLE cheminfra.order_attached DROP CONSTRAINT fk_synthesis_library_order;
--DROP TABLE cheminfra.order_attached;
DROP TABLE cheminfra.synthesis_library_order;

DEFINE tab_space_table = act0_tab
DEFINE tab_space_index = act0_idx

PROMPT CREATING TABLE SYNTHESIS_LIBRARY_ORDER
CREATE TABLE cheminfra.synthesis_library_order (
    id                 NUMBER(8, 0) NOT NULL
        CONSTRAINT pk_cs_order PRIMARY KEY
            USING INDEX TABLESPACE &tab_space_index
                STORAGE ( INITIAL 10K ),
    title              VARCHAR2(255),
    link		       VARCHAR2(510),
    libraryoutcome    VARCHAR2(255),
    project            VARCHAR2(255),
    username           VARCHAR2(255),
    requester          VARCHAR2(255),
    requesttime        DATE,
    fromtime           DATE,
    totime             DATE,
    compound           VARCHAR2(255),
    quantity           NUMBER(32, 0),
    unit               VARCHAR2(255),
    creationtime       DATE,
    updatetime         DATE,
    done               NUMBER(8, 0),
    --status             VARCHAR2(255),
    departmentname     VARCHAR2(255) NOT NULL
)
TABLESPACE &tab_space_table
    STORAGE ( INITIAL 200K );

GRANT SELECT, INSERT, UPDATE, DELETE ON cheminfra.synthesis_library_order TO cheminfrauser;
--DROP SEQUENCE cheminfra.synthesis_library_order_seq ;
CREATE SEQUENCE cheminfra.synthesis_library_order_seq MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER NOCYCLE;
GRANT SELECT ON cheminfra.synthesis_library_order_seq TO cheminfrauser;

--TEST PURPOSE
INSERT INTO cheminfra.synthesis_library_order (
    id,
    title,
    link,
    project,
    username,
    requester,
    fromtime,
    totime,
    creationtime,
    requesttime,
    compound,
    quantity,
    unit,
    departmentname,
    done,
    --status,
    libraryoutcome
) VALUES (
    1,
    'SYNTHESIS SOL.ABC  ',
    NULL,
    'DMPK',
    'BENMEKA1',
    'MANKOKA1',
    To_date('01-01-18','DD-MM-YY'),
    To_date('31-12-32','DD-MM-YY'),
    To_date('01-01-22','DD-MM-YY'),
    To_date('01-01-22','DD-MM-YY'),
    'ACT-814868',
    2,
    'mg',
    'SYNTHESIS',
    0,
    --null,
    null
);

INSERT INTO cheminfra.synthesis_library_order (
    id,
    title,
    link,
    project,
    username,
    requester,
    fromtime,
    totime,
    creationtime,
    requesttime,
    compound,
    quantity,
    unit,
    departmentname,
    done,
    --status,
    libraryoutcome
) VALUES (
    cheminfra.synthesis_library_order_seq.nextval,
    'HTMC SOL.XYZ  ',
    NULL,
    'DMPK',
    'BENMEKA1',
    'MANKOKA1',
    To_date('01-01-18','DD-MM-YY'),
    To_date('31-12-32','DD-MM-YY'),
    To_date('01-01-22','DD-MM-YY'),
    To_date('01-01-22','DD-MM-YY'),
    'ACT-364851',
    32,
    'mg',
    'HTMC',
    0,
    --'Planned',
    'libraryXYZ'
);

--ALTER SEQUENCE CHEMINFRA.SYNTHESIS_ORDER_SEQ RESTART START WITH 5;

alter table CHEMINFRA.SYNTHESIS_LIBRARY_ORDER add REMARKS VARCHAR2(1024);
alter table CHEMINFRA.SYNTHESIS_LIBRARY_ORDER add DONETIME DATE;

COMMIT;