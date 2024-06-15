alter table CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP add GROUP_ID NUMBER(8,0);
alter table CHEMINFRA.INSTRUMENT add EMAILNOTIFICATION NUMBER(1,0) DEFAULT 0;
Set define off;
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 20668 where employee_group_name = 'Preformulation and Preclinical Galenics';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21387 where employee_group_name = 'Clinical Manufacturing for Solids (CMU1)';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21393 where employee_group_name = 'Clinical Manufacturing for Liquids & Semi-solids (CMU2)';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21396 where employee_group_name = 'ADQC Analytical Sciences';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21392 where employee_group_name = 'ADQC Analytical Support Group';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21385 where employee_group_name = 'ADQC Drug Product';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21380 where employee_group_name = 'ADQC Drug Substance';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21395 where employee_group_name = 'ADQC Medical Devices and Combination Products';
update CHEMINFRA.INSTRUMENT_EMPLOYEE_GROUP set group_id = 21397 where employee_group_name = 'ADQC Microbiology';

update CHEMINFRA.INSTRUMENT set EMAILNOTIFICATION = 1 where GROUPNAME = 'PPG';