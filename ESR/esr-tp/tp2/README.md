# TODO
## Etapa 0: Preparação das atividades
- [ ] Preparação das atividades
- [x] Escolher Linguagem -> Java
- [x] Preparar Topologia
- [X] Cenários de teste
- [x] Definir protocolo de transporte no overlay -> UDP
- [X] Implementar um client/server simples (tem de ser simultaneamente client & server) 

## Etapa 1: Construção da Topologia Overlay
- [X] Construir app (oNode) client & server, atende numa porta predefinida, capaz de **receber e enviar** mensagens em modo **full-duplex**
- [X] Testar com envio e receção de mensagens simples de "*HELLO*"
- [ ] Definir uma estratégia de construção de rede *overlay*
    - Estratégia 1: Abordagem manual. Ao executar o programa indicam-se os X vizinhos em configuração ou na linha de comando. Exemplo: `` $ oNode <vizinho1> <vizinho2> <vizinho3>``. A tabela de vizinhos e respetivas conexões não se altera durante a execução. Não há necessidade de nenhum nó de controlo.
    - Estratégia 2: Abordagem baseada num controlador. Ao executar o programa, indica-se apenas um nó como contacto para arranque da rede. Exemplo: `` $ oNode <bootstrapper>``. O novo nó envia mensagem de registo, a identificar-se, e recebe como resposta uma lista de X vizinhos. Cabe ao servidor definir quais são esses vizinhos com base no conhecimento que tem de todo o overlay; que para facilitar esta tarefa, sugere-se que o servidor obtenha essa informação de quem se deve ligar a quem a partir de ficheiro de configuração, construído manualmente. Definir estratégia para abandono, se o nó avisa, se deve registar-se periodicamente, etc..
- [X] Outras estratégias... (sugerir ou procurar alternativas, discutindo-as com o docente);
- [ ] Manter as ligações com os vizinhos ativas, começando por monitorar o seu estado (ver Etapa 3).

## Etapa 2: Serviço de Streaming
- [X] Estratégia 1: Implementar um cliente e um servidor simples com base no código do livro de apoio [1];
- [X] Usar o código do livro de apoio (disponível em Python e em Java) como ponto de partida;
- [X] Adaptar o código se a linguagem de programação escolhida se não for Python ou Java;
- [ ] Com base no exemplo, previamente ajustado e comentado pela equipa docente [2], fazer um servidor capaz de ler o vídeo de um ficheiro e o enviar em pacotes, numerados, para a rede overlay;
- [ ] Com base no exemplo, previamente ajustado pela equipa docente, fazer um cliente capaz de receber pacotes da rede overlay, com um número de sequência, e reproduzir o vídeo numa janela;
- [ ] Usar como vídeo de teste o exemplo do livro movie.Mjpeg [2] (trata-se de um vídeo básico, de fluxo constante, que é uma sequência simples de imagens independentes, a enviar a intervalo de tempo fixo);
- [ ] Estratégia 2: Adaptar o código do livro de apoio para utilizar uma biblioteca alternativa, capaz de ler codecs adicionais, e recorrendo a outros vídeos para difusão.
- [X] Outras estratégias... (sugerir ou procurar alternativas, discutindo-as com o docente).

## Etapa 3: Monitorização da Rede Overlay
- [ ] Para além do que será a rede de entrega de conteúdos, pretende-se que cada servidor da topologia difunda periodicamente uma mensagem de prova (teste) na rede que irá permitir obter um conhecimento razoavelmente atualizado das condições de entrega na rede overlay. Cada nó que receba a mensagem deve reenviá-la a todos os vizinhos, com o cuidado de evitar repetições em ciclo e de enviar a quem lhe enviou a ele (inundação controlada, por exemplo enviando só uma vez cada mensagem, evitando enviar para trás pela conexão de onde recebeu). Essa mensagem pode ser estruturada incluindo a identificação do servidor, o nº de saltos que a mensagem dá e o instante temporal em que a mesma foi enviada, para que possa ser calculado o atraso sofrido desde que o seu envio até à sua receção. Como sugestão, registe também a interface que conduz à melhor rote de volta à fonte. Desta forma, cada nó da rede terá sempre conhecimento do número de nós envolvidos até ao servidor (fonte de dados) e uma estimativa do atraso na ligação. Se entender pode usar outras métricas adicionais. Como teste inicial, pode considerar apenas um dos servidores (S1), definir atrasos de, por exemplo, 10ms por ligação (para facilitar pode introduzir o atraso em ligações da rede underlay), e verificar as métricas observadas por nó da overlay. Uma vez testada e operacional, pode estender a monitorização da rede ao servidor S2. Note que com este Universidade do Minho MEI, Engenharia de Serviços em Rede 2022/2023 GCOM.DI.UMINHO.PT proibido o uso não autorizado Pág 4 de 6 tipo de controlo, os nós overlay folha (de acesso direto aos clientes) passam a conhecer qual dos servidores está em melhores condições para realizar a entrega de conteúdo.

## Etapa 4: Construção de Rotas para a Entrega de Dados
- [ ] Esta etapa pode ser vista como uma extensão da etapa anterior.
- [ ] Para escolha da melhor rota, cada nó da overlay deve considerar a métrica mais favorável; por exemplo, o menor atraso, e para atrasos idênticos, o menor número de saltos.
- [ ] Estratégia 1: Por iniciativa do servidor de streaming, com anúncios periódicos.
- [ ] Servidor envia mensagem de controlo com anúncio; mensagem inclui identificação do servidor, identificação do fluxo, etc.;
- [ ] Ao receber a mensagem cada nó constrói uma tabela de rotas, com informação de Servidor, Fluxo, Origem, Métrica, Destinos, Estado da Rota; até serem necessárias as rotas ficam num estado inativo.
- [ ] Cada nó que receba a mensagem deve enviá-la a todos os vizinhos, usando inundação controlada;
- [ ] Antes de reenviar a mensagem, a métrica deve ser atualizada;
- [ ] As entradas estão inicialmente inativas, sendo ativadas pelo cliente quando se liga, enviado uma mensagem de ativação da rota, pelo percurso inverso;
- [ ] Cada mensagem com pedido de ativação do cliente deve ser reencaminhada em cada nó seguido o campo “Origem” da rota;
- [ ] O cliente só envia um pedido de ativação, quando deseja receber os dados; não havendo clientes, as rotas existem nas tabelas, mas estão inativas, não havendo tráfego;
- [ ] Estratégia 2: Por iniciativa do Cliente de streaming, enviando um pedido diretamente ao servidor
- [ ] A iniciativa pode ser do cliente que pretende receber a stream, enviando um pedido explícito de rota para essa stream;
- [ ] Esta estratégia pressupõe que o servidor tem informação de registo e conhece o overlay, podendo por isso escolher o percurso e ensiná-lo ao cliente, que usa a informação obtida para efetivar o percurso;
- [ ] Se o servidor não conhece a topologia, o pedido do cliente tem de ser enviado para todos (inundação controlada) até chegar a um nó que já tem informação da stream;
- [ ] Alternativamente podem ser criadas rotas estáticas na overlay.
- [ ] Estratégia 3: Construção de uma infraestrutura de entrega (árvore) partilhada
- [ ] Nesta estratégia deve ser designado um nó overlay específico (rendezvous point (RP)) a partir do qual são criadas as rotas de entrega recorrendo à Estratégia 1 ou Estratégia 2. Neste cenário, os servidores S1 e S2 enviam a stream multimédia em unicast ao RP, efetuando-se a difusão do fluxo a partir daí. Como no presente serviço de streaming, os servidores disponibilizam o mesmo conteúdo, esse conteúdo deve ser enviado apenas pelo servidor que apresente melhores métricas no caminho até ao RP.
- [ ] Outras estratégias... (sugerir ou procurar alternativas, discutindo-as com o docente).

## Etapa 5: Ativação e Teste do Servidor Alternativo
- [ ] O objetivo desta etapa é forçar a ativação do servidor alternativo de forma transparente ao cliente quando as condições na entrega da stream se degradam. Para isso pode-se provocar uma situação adversa na rede overlay introduzindo um atraso excessivo num das ligações da rede underlay (para simplificar) que afete a entrega do fluxo em curso. Desta forma, com base nos resultados de monitorização, um nó da overlay adjacente a um cliente ativo pode, no interesse do cliente, abandonar a árvore com origem em S1 e ativar as rotas necessárias para passar a receber o conteúdo de S2. No caso de ser usada uma árvore de entrega partilhada, a decisão de ativação ou não do servidor alternativo deve ser tomada no RP.

## Etapa Opcional: Definição do método de recuperação de falhas
- [ ] Estratégia 1: Cálculo e redistribuição de rotas centralizado
- [ ] Iniciativa pode partir dos nós adjacentes no overlay, ou do servidor central, dependendo do método de monitorização de serviço implementado;
- [ ] Deve assegurar que as rotas calculadas são as mais eficientes para a nova configuração do overlay;
- [ ] Poderão ser testados diversos métodos de convergência, de modo a minimizar o período em que o serviço não está disponível.
- [ ] Estratégia 2: Restabelecimento de rotas por inundação controlada
- [ ] Estratégia com potencial para recuperar muito mais rapidamente do que uma implementação centralizada, porém mais desafiante tecnicamente;
- [ ] A iniciativa partirá necessariamente dos nós remanescentes no overlay;
- [ ] Podem ser implementados métodos para minimizar o número de pacotes trocados;
- [ ] A estratégia adotada para a criação de novas ligações pode ter também uma componente de profundidade para que o seu estabelecimento seja o mais célere possível.
- [ ] Outras estratégias a discutir com o docente.