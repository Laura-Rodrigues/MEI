Redis funciona como um mecanismo de cache



# Questions 

1. What is this application's architecture and what pattern(s) are present?

Multi-tier. A vm2 está conectada com a base de dados e a vm1 faz a ligação 
entre a vm2 e um browser, ou seja, conecta a base de dados com o cliente.

2. What would you expect the bottleneck of this application to be? Why?


3. How would you scale this application? Which patterns would you use? Why?
