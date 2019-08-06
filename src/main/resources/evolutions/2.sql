# --- !Ups

create table outra
(
    cod_estado      varchar(2)  not null,
    nome            varchar(60) not null,
    cod_estado_ibge integer     not null
);

# --- !Downs

DROP TABLE licenciamento.outra;