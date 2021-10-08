## API Cadastro de Jogadores

### Intro
O objetivo deste projeto é desenvolver uma API a partir de **TDD** (Test Driven Development).

### Case
Criar um sistema de cadastro de jogadores de uma determinada empresa.
Para isso deve-se elaborar um sistema em Java capaz de:

- Apresentar um cadastro contendo nome, e-mail, telefone do jogador, codinome e grupo ao qual pertencem, sendo que o codinome deve se referir a um personagem do grupo dos "Vingadores" ou "Liga da Justiça";
- Persistir a informação cadastrada em um banco de dados em memória;
- Obter, a qualquer momento, a lista de todos os jogadores cadastrados com seus respectivos codinomes e o grupo ao qual pertencem;
- Impedir a utilização de um mesmo codinome para diferentes usuários.

___________

### Tecnologias utilizadas:
- Java 11 (ou versões superiores);
- Maven 3.6.3 ou versões superiores;
- Spring Data JPA;
- Spring Web;
- Spring Boot;
- H2 (banco em memória);
- Swagger 2 (Documentaçao).
___

### Visão Geral da API
- POST (Exemplo do body para cadastro)
```
{
  "codinome": "string",
  "email": "string",
  "grupo": "string",
  "id": 0,
  "nome": "string",
  "telefone": "string"
}
```
- GET (Exemplo de corpo de resposta por ID)
```
{
  "codinome": "string",
  "email": "string",
  "grupo": "string",
  "id": 0,
  "nome": "string",
  "telefone": "string"
}
```
- GET (Exemplo de corpo de resposta - findAll)
```
{  "content":  [  {  "codinome":  "string",  "email":  "string",  "grupo":  "string",  "id":  0,  "nome":  "string",  "telefone":  "string"  }  ],  "empty":  true,  "first":  true,  "last":  true,  "number":  0,  "numberOfElements":  0,  "pageable":  {  "offset":  0,  "pageNumber":  0,  "pageSize":  0,  "paged":  true,  "sort":  {  "empty":  true,  "sorted":  true,  "unsorted":  true  },  "unpaged":  true  },  "size":  0,  "sort":  {  "empty":  true,  "sorted":  true,  "unsorted":  true  },  "totalElements":  0,  "totalPages":  0  }
```

