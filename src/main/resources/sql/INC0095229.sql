DEFINE tab_space_table=ACT0_TAB
DEFINE tab_space_index=ACT0_IDX

PROMPT Creating TABLE INSTRUMENT_DEPUTY
CREATE TABLE CHEMINFRA.INSTRUMENT_DEPUTY (
    ID NUMBER(8,0) NOT NULL
		CONSTRAINT pk_instrument_deputy
		PRIMARY KEY
		USING INDEX TABLESPACE &tab_space_index
		STORAGE (INITIAL 10k),
	INSTRUMENT_ID	NUMBER(8,0)	NOT NULL
        CONSTRAINT fk_instrument_deputy_id
        REFERENCES CHEMINFRA.INSTRUMENT(ID),
    DEPUTY VARCHAR2(64)
	)
	TABLESPACE &TAB_SPACE_TABLE
    STORAGE (INITIAL 200K);
    
GRANT SELECT, INSERT, UPDATE, DELETE ON CHEMINFRA.INSTRUMENT_DEPUTY TO CHEMINFRAUSER;    
    
CREATE SEQUENCE CHEMINFRA.INSTRDEPUTY_SEQ MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
GRANT SELECT ON CHEMINFRA.INSTRDEPUTY_SEQ to CHEMINFRAUSER;    

--old deputies
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,460,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,1060,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,1180,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,420,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,206,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,207,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,205,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,480,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,46,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,48,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,49,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,601,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,602,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,603,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,600,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,400,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,920,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,1040,'RIBICV');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,1080,'BARTENC');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,1281,'MENYHAK');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,1301,'MENYHAK');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,1229,'ERUPAJU1');


--TECAN
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,541,'KURATLN');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,541,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,541,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,50,'KURATLN');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,50,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,50,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,540,'KURATLN');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,540,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,540,'VOGELSR');

--prep_system
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,42,'KURATLN');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,42,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,42,'HOFSTEB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,42,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,43,'HOFSTEB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,43,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,43,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,381,'KURATLN');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,381,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,381,'HOFSTEB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,381,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,320,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,320,'HOFSTEB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,320,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,380,'KURATLN');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,380,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,380,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,500,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,500,'HOFSTEB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,500,'VOGELSR');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,940,'KURATLN');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,940,'MATHYSB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,940,'HOFSTEB');
Insert into CHEMINFRA.INSTRUMENT_DEPUTY (ID,INSTRUMENT_ID,DEPUTY) values (CHEMINFRA.INSTRDEPUTY_SEQ.nextval,940,'VOGELSR');

ALTER TABLE CHEMINFRA.INSTRUMENT DROP COLUMN DEPUTY;

commit;


