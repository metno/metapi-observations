#!/bin/sh

# Generates 1.sql


echo '# --- !Ups' > 1.sql

cat original.sql |
	sed 's/NUMBER[(]10[)]/int/g' |\
	sed 's/VARCHAR2[(]150 BYTE[)]/text/g' |\
	sed 's/VARCHAR2[(]200 BYTE[)]/text/g' |\
	sed 's/VARCHAR2[(]100 BYTE[)]/text/g' |\
	sed 's/VARCHAR2[(]200 CHAR[)]/text/g' |\
	sed 's/VARCHAR2[(]10 BYTE[)]/text/g' |\
	sed 's/VARCHAR2[(]2 BYTE[)]/text/g' |\
	sed 's/KPORTAL[.]//g' |\
	iconv -tUTF-8 -fISO-8859-1 >>\
	1.sql


echo '# --- !Downs' >> 1.sql
echo 'DROP TABLE T_KDVH_USEINFO_FLAG;' >> 1.sql
echo 'DROP TABLE T_KDVH_USER_FLAG;' >> 1.sql
