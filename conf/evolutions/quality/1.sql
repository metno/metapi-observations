# --- !Ups
CREATE TABLE T_KDVH_USEINFO_FLAG
(
  USEINFO_ID    int                      NOT NULL,
  USEINFO_NAME text              NOT NULL,
  USEINFO_FLAG  int                      NOT NULL,
  DESCRIPTION   text              NOT NULL,
  LANGUAGE          text
);
GRANT SELECT ON T_KDVH_USEINFO_FLAG TO PUBLIC;

Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 0, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 1, 'QC1 (realtime control), QC2 (by time control) and HQC (manual control) are completed', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 2, 'QC2 (by time control) and HQC (manual control) are completed (not QC1 (realtime control))', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 3, 'QC1 (realtime control) and HQC (manual control) are completed (not whole QC2 (by time control))', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 4, 'HQC (manual control) is completed (not QC1 (realtime control), not whole QC2 (by time control))', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 5, 'QC1 (realtime control) and QC2 (by time control) are completed (not HQC (manual control))', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 6, 'QC2 (by time control) is completed (not QC1 (realtime control), not HQC (manual control))', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 7, 'QC1 (realtime control) is completed (not whole QC2 (by time control), not HQC (manual control))', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 8, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Control level passed', 9, 'Information about level of control is not given', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 0, 'Normed observation period and time of observation ', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 1, 'Time of observation deviates from the norm', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 2, 'Observation period is shorter than normed', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 3, 'Observation period is longer than normed', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 4, 'Time of observation deviates from the norm, observation period is shorter than normed', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 5, 'Time of observation deviates from the norm, observation period is longer than normed', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 6, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 7, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 8, 'The original value is missing', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'The original value deviate from normed observation procedure', 9, 'Status information is not given', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 0, 'The original value is found to be okay.', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 1, 'The original value is some uncertain (presumably correct)', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 2, 'The original value is very uncertain', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 3, 'The original value is presumably erronous', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 4, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 5, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 6, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 7, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 8, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Qualitylevel for the original value', 9, 'Quality information is not given', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 0, 'Not corrected', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 1, 'Original value is manually corrected, or automatic corrected med godt resultat', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 2, 'Original value is manually interpolated, or automatic interpolert med godt resultat', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 3, 'Automatic corrected', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 4, 'Automatic interpolated', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 5, 'Manually given from accumulated value', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 6, 'Automatic processed from accumulated value', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 7, 'Reserved', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 8, 'Rejected', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Original value corrected', 9, 'Quality information not given', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 0, 'Original value controlled and found to be okay', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 1, 'Threshold value control', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 2, 'Consistency control (more than one parameter)', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 3, 'Leap control (one parameter)', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 4, 'Consistency control according to earlier or later observations (more than one parameter)', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 5, 'Space control, based on observation data', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 6, 'Space control, based on timeseries', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 7, 'Space control, based on model data', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 8, 'Space control, based on statistics', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Most important quality control method', 9, 'Quality information not given', 'en');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 0, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 1, 'QC1 (sanntidskontroll), QC2 (ettertidskontroll) og HQC (manuell kontroll) er gjennomført', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 2, 'QC2 (ettertidskontroll) og HQC (manuell kontroll) er gjennomført (ikkje QC1 (sanntidskontroll))', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 3, 'QC1 (sanntidskontroll) og HQC (manuell kontroll) er gjennomført (ikkje heile QC2 (ettertidskontroll))', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 4, 'HQC (manuell kontroll) er gjennomført (ikkje QC1 (sanntidskontroll), ikkje heile QC2 (ettertidskontroll))', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 5, 'QC1 (sanntidskontroll) og QC2 (ettertidskontroll) er gjennomført (ikkje HQC (manuell kontroll))', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 6, 'QC2 (ettertidskontroll) er gjennomført (ikkje QC1 (sanntidskontroll), ikkje HQC (manuell kontroll))', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 7, 'QC1 (sanntidskontroll) er gjennomført (ikkje heile QC2 (ettertidskontroll), ikkje HQC (manuell kontroll))', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 8, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 9, 'Informasjon om kontrollnivå ikkje gitt', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 0, 'Normert observasjonsperiode og observasjonstid', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 1, 'Observasjonstid avviker frå normen', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 2, 'Observasjonsperiode er kortere enn normert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 3, 'Observasjonsperiode er lengre enn normert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 4, 'Observasjonstid avvik frå normen, observasjonsperiode er kortere enn normert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 5, 'Observasjonstid avvik frå normen, observasjonsperiode er lengre enn normert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 6, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 7, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 8, 'Originalverdi manglar', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdien avvik frå normert observasjonsprosedyre', 9, 'Statusinformasjon ikkje gitt', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 0, 'Originalverdi funnen i orden', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 1, 'Originalverdi noko mistenkelig (antakeleg korrekt)', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 2, 'Originalverdi svært mistenkelig (antakeleg feilaktig)', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 3, 'Originalverdi sikkert feilaktig', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 4, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 5, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 6, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 7, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 8, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 9, 'Kvalitetsinformasjon ikkje gitt', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 0, 'Ikkje korrigert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 1, 'Original verdi er manuelt korrigert, eller automatisk
korrigert med godt resultat', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 2, 'Original verdi er manuelt interpolert, eller automatisk
interpolert med godt resultat', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 3, 'Automatisk korrigert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 4, 'Automatisk interpolert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 5, 'Manuelt tilfordelt frå akkumulert verdi', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 6, 'Automatisk tilfordelt frå akkumulert verdi', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 7, 'Reservert', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 8, 'Forkasta', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 9, 'Kvalitetsinformasjon ikkje gitt', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 0, 'Original verdi kontrollert og funne i orden', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 1, 'Grenseverdikontroll', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 2, 'Konsistenskontroll (meir enn ein parameter)', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 3, 'Sprangkontroll (ein parameter)', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 4, 'Konsistenskontroll i forhold til tidlegare/senare observasjonsterminer (meir enn ein parameter)', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 5, 'Romkontroll, basert på observasjonsdata', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 6, 'Romkontroll, basert på tidsserier', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 7, 'Romkontroll, basert på modelldata', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 8, 'Romkontroll, basert på statistikk', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigaste kontrollmetode', 9, 'Kvalitetsinformasjon ikkje gitt', 'es');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 0, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 1, 'QC1 (sanntidskontroll), QC2 (ettertidskontroll) og HQC (manuell kontroll) er gjennomført', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 2, 'QC2 (ettertidskontroll) og HQC (manuell kontroll) er gjennomført (ikke QC1 (sanntidskontroll))', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 3, 'QC1 (sanntidskontroll) og HQC (manuell kontroll) er gjennomført (ikke hele QC2 (ettertidskontroll))', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 4, 'HQC (manuell kontroll) er gjennomført (ikke QC1 (sanntidskontroll), ikke hele QC2 (ettertidskontroll))', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 5, 'QC1 (sanntidskontroll) og QC2 (ettertidskontroll) er gjennomført (ikke HQC (manuell kontroll))', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 6, 'QC2 (ettertidskontroll) er gjennomført (ikke QC1 (sanntidskontroll), ikke HQC (manuell kontroll))', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 7, 'QC1 (sanntidskontroll) er gjennomført (ikke hele QC2 (ettertidskontroll), ikke HQC (manuell kontroll))', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 8, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (0, 'Kontrollnivå passert', 9, 'Informasjon om kontrollnivå ikke gitt', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 0, 'Normert observasjonsperiode og observasjonstid', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 1, 'Observasjonstid avviker fra normen', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 2, 'Observasjonsperiode er kortere enn normert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 3, 'Observasjonsperiode er lengre enn normert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 4, 'Observasjonstid avviker fra normen, observasjonsperiode er kortere enn normert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 5, 'Observasjonstid avviker fra normen, observasjonsperiode er lengre enn normert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 6, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 7, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 8, 'Originalverdi mangler', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (1, 'Originalverdiens avvik fra normert observasjonsprosedyre', 9, 'Statusinformasjon ikke gitt', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 0, 'Originalverdi funnet i orden', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 1, 'Originalverdi noe mistenkelig (antagelig korrekt)', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 2, 'Originalverdi svært mistenkelig (antagelig feilaktig)', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 3, 'Originalverdi sikkert feilaktig', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 4, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 5, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 6, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 7, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 8, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (2, 'Kvalitetsnivå for originalverdi', 9, 'Kvalitetsinformasjon ikke gitt', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 0, 'Ikke korrigert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 1, 'Original verdi er manuelt korrigert, eller automatisk
korrigert med godt resultat', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 2, 'Original verdi er manuelt interpolert, eller automatisk
interpolert med godt resultat', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 3, 'Automatisk korrigert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 4, 'Automatisk interpolert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 5, 'Manuelt tilfordelt fra akkumulert verdi', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 6, 'Automatisk tilfordelt fra akkumulert verdi', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 7, 'Reservert', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 8, 'Forkastet', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (3, 'Originalverdi korrigert', 9, 'Kvalitetsinformasjon ikke gitt', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 0, 'Original verdi kontrollert og funnet i orden', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 1, 'Grenseverdikontroll', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 2, 'Konsistenskontroll (mer enn én parameter)', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 3, 'Sprangkontroll (én parameter)', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 4, 'Konsistenskontroll i forhold til tidligere/senere observasjonsterminer (mer enn én parameter)', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 5, 'Romkontroll, basert på observasjonsdata', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 6, 'Romkontroll, basert på tidsserier', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 7, 'Romkontroll, basert på modelldata', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 8, 'Romkontroll, basert på statistikk', 'no');
Insert into T_KDVH_USEINFO_FLAG
   (USEINFO_ID, USEINFO_NAME, USEINFO_FLAG, DESCRIPTION, LANGUAGE)
 Values
   (4, 'Viktigste kontrollmetode', 9, 'Kvalitetsinformasjon ikke gitt', 'no');


CREATE TABLE T_KDVH_USER_FLAG
(
  FLAG_LEVEL        int                  NOT NULL,
  FLAG_TEXT         text,
  FLAG_CODE         text,
  LANGUAGE          text,
  FLAG_DESCRIPTION  text
);
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (-2, ' ', ' ', 'Reserved - not in use.', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (-2, ' ', ' ', 'Reservert - ikkje bruka', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (-2, ' ', ' ', 'Reservert - ikke i bruk', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (0, 'OK', 'OK', 'Value is controlled and found O.K.', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (0, 'OK', 'OK', 'Verdi er kontrollert og funne grei.', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (0, 'OK', 'OK', 'Verdi er kontrollert og funnet i orden.', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (1, 'OK', 'OK', 'Value is controlled and corrected, or value is missing and interpolated.', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (1, 'OK', 'OK', 'Verdi er kontrollert og korrigert, eller verdi manglar og er interpolert.', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (1, 'OK', 'OK', 'Verdi er kontrollert og korrigert, eller verdi mangler og er interpolert.', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (2, 'LU', 'Slightly uncertain', 'Value is not controlled.', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (2, 'LU', 'Litt uviss', 'Verdi er ikkje kontrollert.', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (2, 'LU', 'Litt usikker', 'Verdi er ikke kontrollert.', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (3, 'LU', 'Slightly uncertain', 'Reserved - not in use.', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (3, 'LU', 'Litt uviss', 'Reservert - ikkje bruka', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (3, 'LU', 'Litt usikker', 'Reservert - ikke i bruk', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (4, 'LU', 'Slightly uncertain', 'Value is slightly uncertain (not corrected).', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (4, 'LU', 'Litt uviss', 'Verdi er litt uviss (ikkje korrigert).', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (4, 'LU', 'Litt usikker', 'Verdi er litt usikker (ikke korrigert).', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (5, 'SU', 'Very uncertain', 'Value is very uncertain (not corrected).', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (5, 'SU', 'Særs uviss', 'Verdi er veldig uviss (ikkje korrigert).', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (5, 'SU', 'Svært usikker', 'Verdi er svært usikker (ikke korrigert).', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (6, 'SU-M', 'Very uncertain, modeldata', 'Value is controlled and corrected, or value is missing and is interpolated - automatic.', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (6, 'SU-M', 'Særs uviss, modelldata', 'Verdi er kontrollert og korrigert, eller originalverdi manglar og er interpolert - automatisk.', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (6, 'SU-M', 'Svært usikker, modelldata', 'Verdi er kontrollert og korrigert, eller originalverdi mangler og er interpolert - automatisk.', 'no');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (7, 'FE', 'Erroneous', 'Value is erroneous (not corrected).', 'en');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (7, 'FE', 'Feil', 'Verdi er feil (ikkje korrigert).', 'es');
Insert into T_KDVH_USER_FLAG
   (FLAG_LEVEL, FLAG_CODE, FLAG_TEXT, FLAG_DESCRIPTION, LANGUAGE)
 Values
   (7, 'FE', 'Feilaktig', 'Verdi er feil (ikke korrigert).', 'no');
# --- !Downs
DROP TABLE T_KDVH_USEINFO_FLAG;
DROP TABLE T_KDVH_USER_FLAG;
