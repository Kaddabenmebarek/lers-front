DEFINE tab_space_table=ACT0_TAB
DEFINE tab_space_index=ACT0_IDX

PROMPT Creating TABLE INSTRUMENT_EMPLOYEE_GROUP
CREATE TABLE CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP (
    ID NUMBER(8,0) NOT NULL
		CONSTRAINT pk_instrument_employee_group
		PRIMARY KEY
		USING INDEX TABLESPACE &tab_space_index
		STORAGE (INITIAL 10k),
	INSTRUMENT_ID	NUMBER(8,0)	NOT NULL
        CONSTRAINT fk_instrument_employee_group_id
        REFERENCES CHEMINFRA.INSTRUMENT(ID),
    EMPLOYEE_GROUP_NAME VARCHAR2(128)
	)
	TABLESPACE &TAB_SPACE_TABLE
    STORAGE (INITIAL 200K);
    
GRANT SELECT, INSERT, UPDATE, DELETE ON CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP TO CHEMINFRAUSER;  

CREATE SEQUENCE CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
GRANT SELECT ON CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ to CHEMINFRAUSER;

PROMPT Creating TABLE INSTRUMENT_RESTRICTION
CREATE TABLE CHEMINFRA.INSTRUMENT_RESTRICTION (
    ID NUMBER(8,0) NOT NULL
		CONSTRAINT pk_instrument_restriction
		PRIMARY KEY
		USING INDEX TABLESPACE &tab_space_index
		STORAGE (INITIAL 10k),
	INSTRUMENT_ID	NUMBER(8,0)	NOT NULL
        CONSTRAINT fk_instrument_restriction_id
        REFERENCES CHEMINFRA.INSTRUMENT(ID),
    RESTRICTED_INSTRUMENT_ID NUMBER(8,0)
	)
	TABLESPACE &TAB_SPACE_TABLE
    STORAGE (INITIAL 200K);
    
GRANT SELECT, INSERT, UPDATE, DELETE ON CHEMINFRA.INSTRUMENT_RESTRICTION TO CHEMINFRAUSER;  

CREATE SEQUENCE CHEMINFRA.INSTRUMENT_RESTRICTION_SEQ MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
GRANT SELECT ON CHEMINFRA.INSTRUMENT_RESTRICTION_SEQ to CHEMINFRAUSER;

Set define off;
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'Preformulation and Preclinical Galenics');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'Clinical Manufacturing for Solids (CMU1)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'Clinical Manufacturing for Liquids & Semi-solids (CMU2)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'ADQC Analytical Sciences');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'ADQC Analytical Support Group');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'ADQC Drug Product');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'ADQC Drug Substance');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'ADQC Medical Devices and Combination Products');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1201', 'ADQC Microbiology');

INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'Preformulation and Preclinical Galenics');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'Clinical Manufacturing for Solids (CMU1)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'Clinical Manufacturing for Liquids & Semi-solids (CMU2)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'ADQC Analytical Sciences');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'ADQC Analytical Support Group');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'ADQC Drug Product');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'ADQC Drug Substance');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'ADQC Medical Devices and Combination Products');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1202', 'ADQC Microbiology');

INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'Preformulation and Preclinical Galenics');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'Clinical Manufacturing for Solids (CMU1)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'Clinical Manufacturing for Liquids & Semi-solids (CMU2)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'ADQC Analytical Sciences');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'ADQC Analytical Support Group');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'ADQC Drug Product');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'ADQC Drug Substance');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'ADQC Medical Devices and Combination Products');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1203', 'ADQC Microbiology');

INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'Preformulation and Preclinical Galenics');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'Clinical Manufacturing for Solids (CMU1)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'Clinical Manufacturing for Liquids & Semi-solids (CMU2)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'ADQC Analytical Sciences');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'ADQC Analytical Support Group');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'ADQC Drug Product');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'ADQC Drug Substance');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'ADQC Medical Devices and Combination Products');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1204', 'ADQC Microbiology');

INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'Preformulation and Preclinical Galenics');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'Clinical Manufacturing for Solids (CMU1)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'Clinical Manufacturing for Liquids & Semi-solids (CMU2)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'ADQC Analytical Sciences');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'ADQC Analytical Support Group');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'ADQC Drug Product');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'ADQC Drug Substance');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'ADQC Medical Devices and Combination Products');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1205', 'ADQC Microbiology');

INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'Preformulation and Preclinical Galenics');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'Clinical Manufacturing for Solids (CMU1)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'Clinical Manufacturing for Liquids & Semi-solids (CMU2)');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'ADQC Analytical Sciences');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'ADQC Analytical Support Group');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'ADQC Drug Product');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'ADQC Drug Substance');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'ADQC Medical Devices and Combination Products');
INSERT INTO "CHEMINFRA"."INSTRUMENT_EMPLOYEE_GROUP" (ID, INSTRUMENT_ID, EMPLOYEE_GROUP_NAME) VALUES (CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP_SEQ.nextval, '1206', 'ADQC Microbiology');

INSERT INTO "CHEMINFRA"."INSTRUMENT_RESTRICTION" (ID, INSTRUMENT_ID, RESTRICTED_INSTRUMENT_ID) VALUES (CHEMINFRA.INSTRUMENT_RESTRICTION_SEQ.nextval, '1321', '1322');
INSERT INTO "CHEMINFRA"."INSTRUMENT_RESTRICTION" (ID, INSTRUMENT_ID, RESTRICTED_INSTRUMENT_ID) VALUES (CHEMINFRA.INSTRUMENT_RESTRICTION_SEQ.nextval, '1322', '1321');

alter table CHEMINFRA.INSTRUMENT add RATIOCOMMENT VARCHAR2(1024 BYTE);

--select sequence_name from ALL_SEQUENCES where sequence_owner = 'CHEMINFRA';

Insert into CHEMINFRA.INSTRUMENT (ID,NAME,DESCRIPTION,STATUS,LOCATION,RESERVABLE,USERNAME,GROUPNAME,INFOTITLE,INFOMESSAGE,ISPUBLIC,SELECTOVERLAP,RATIOCOMMENT) values (CHEMINFRA.INSTRUMENT_SEQ.nextval,'instrumentA','DescA','Working','H91.O4.E04','1','BENMEKA1','PPG',null,null,'1','0',null);
Insert into CHEMINFRA.INSTRUMENT (ID,NAME,DESCRIPTION,STATUS,LOCATION,RESERVABLE,USERNAME,GROUPNAME,INFOTITLE,INFOMESSAGE,ISPUBLIC,SELECTOVERLAP,RATIOCOMMENT) values (CHEMINFRA.INSTRUMENT_SEQ.nextval,'instrumentB','DescB','Working','H91.01.E04','1','BENMEKA1','PPG',null,null,'1','0',null);

commit