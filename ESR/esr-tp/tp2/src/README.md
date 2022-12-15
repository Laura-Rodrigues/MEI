# Trabalho Prático 2 - Serviço Over the Top para entrega de multimédia

## Engenharia de Serviços em Rede

Este projeto visa dar resposta ao trabalho prático 2 proposto pelos docentes.

Para compilar:

* javac Teste_ott.java

Para correr:

* No CORE Network Emulator carregar um dos ficheiros .imn disponibilizados na pasta de topologias
* A cada topologia correspondem os bootstrapper:

- streaming.imn -> bootstrapper.txt
- streaming2.imn -> bootstrapper4.txt (para o servidor n3) e bootstrapper5.txt (para n4)
- topologia\_tp2\_esr.imn -> bootstrapper3.txt


Servidores:
- java Teste_ott Server "bootstrapper.txt" <ip\_server>

Nodos:
- java Teste_ott <ip\_nodo> <ip\_server>

Clientes:
- java Teste_ott Client <ip\_cliente> <ip\_server>
