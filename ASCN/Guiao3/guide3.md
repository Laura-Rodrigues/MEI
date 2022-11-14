# Guião 3

### Correr o vagrantfile e entrar na VM usando o ssh

* vagrant up
* ssh vagrant@192.168.56.101



## Install Docker 

### Limpar versões anteriores

* sudo apt-get remove docker docker-engine docker.io containerd runc

### Set up the repository

* sudo apt-get update
* sudo apt-get install \
    ca-certificates \
    curl \
    gnupg \
    lsb-release
* sudo mkdir -p /etc/apt/keyrings
* curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
* echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

### Install Docker Engine

* sudo apt-get update
* sudo apt-get install docker-ce docker-ce-cli containerd.io docker-compose-plugin
* sudo docker run hello-world

## Run the sample application 

### Get the app

* git clone https://github.com/docker/getting-started.git

### Build the app’s container image

* cd getting-started/app/
* touch Dockerfile
* vim Dockerfile

```
# syntax=docker/dockerfile:1
FROM node:12-alpine
RUN apk add --no-cache python2 g++ make
WORKDIR /app
COPY . .
RUN yarn install --production
CMD ["node", "src/index.js"]
EXPOSE 3000
```

* sudo docker build -t getting-started .

### Start an app container

* sudo docker run -dp 3000:3000 getting-started

Abrir no browser: 192.168.56.101:3000

# Network

## Tasks

* sudo docker network create network_ascn
* sudo docker network list
* sudo docker network inspect network_ascn

# Database

## Tasks
* sudo docker image pull mysql:latest
  - se não funcionar -> sudo docker image pull mysql
* sudo docker run --name db_content --net network_ascn -p 3306:3306 -e MYSQL_USER=teste -e MYSQL_PASSWORD=123456 -e MYSQL_DATABASE=swap -e MYSQL_ALLOW_EMPTY_PASSWORD=true -d mysql:latest
* sudo docker exec -ti db_content /bin/sh 
* sh-4.4#     mysqladmin --host=0.0.0.0 --user=teste --password=123456 status
* sh-4.4#     exit

# Swap components

**dockerfile2**

* cd getting-started/app
* mv Dockerfile Dockerfile1
* touch Dockerfile
* vim Dockerfile

```
FROM ubuntu:20.04

# Define default values for the Enviroment Variables
ENV DB_HOST db_content
ENV DB_DATABASE swap
ENV DB_USERNAME teste
ENV DB_PASSWORD 123456

RUN apt-get update && \
    apt-get install -y software-properties-common

# Install app packages
RUN add-apt-repository ppa:ondrej/php && apt-get update && apt-get install -y \
   php7.4 \
   php7.4-fpm \
   php7.4-zip \
   php7.4-mbstring \
   php7.4-tokenizer \
   php7.4-mysql \
   php7.4-gd \
   php7.4-xml \
   php7.4-bcmath \
   php7.4-intl \
   php7.4-curl \
   nodejs \
   composer \
   npm

# Clone Swap repository
RUN git clone https://github.com/Hackathonners/swap.git
WORKDIR swap

# Create Swap configuration file
RUN cp .env.example .env
RUN sed -i 's/DB_HOST=127.0.0.1/DB_HOST=${DB_HOST}/g' .env
RUN sed -i 's/DB_DATABASE=homestead/DB_DATABASE=${DB_DATABASE}/g' .env
RUN sed -i 's/DB_USERNAME=homestead/DB_USERNAME=${DB_USERNAME}/g' .env
RUN sed -i 's/DB_PASSWORD=secret/DB_PASSWORD=${DB_PASSWORD}/g' .env

# Install Swap composer
RUN composer install

# Install Swap npm
RUN npm install

# Generate key Swap
RUN php artisan key:generate

# Expose port 8000
EXPOSE 8000

# Start Swap server
#seed e migrate desnecessários em algumas situações
CMD php artisan migrate && php artisan serve --host=0.0.0.0
```

Estar dentro da pasta getting-started/app.
Verificar quais os processos a correr e, se necessário, remover os que não sejam da bd

* sudo docker ps 
* docker container rm -f <id do container id>

```
vagrant@VM1:~/app/getting-started$ docker ps -all

CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS                                                  NAMES
ef2542eab47b   mysql:latest   "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   0.0.0.0:3306->3306/tcp, :::3306->3306/tcp, 33060/tcp   db_content

```

Build da Swap Docker image.

* sudo docker build . -t image_swap

* sudo docker run --net network_ascn -p 8000:8000 --name swap -d image_swap

* Abrir no browser: 192.168.56.101:8000

# Extras

* mv Dockerfile Dockerfile2
* touch Dockerfile
* vim Dockerfile

```
FROM ubuntu:20.04

# Define default values for the Enviroment Variables
ENV MIGRATE false
ENV SEED false

RUN apt-get update && \
    apt-get install -y software-properties-common

# Install app packages
RUN add-apt-repository ppa:ondrej/php && apt-get update && apt-get install -y \
   php7.4 \
   php7.4-fpm \
   php7.4-zip \
   php7.4-mbstring \
   php7.4-tokenizer \
   php7.4-mysql \
   php7.4-gd \
   php7.4-xml \
   php7.4-bcmath \
   php7.4-intl \
   php7.4-curl \
   nodejs \
   composer \
   npm 
   

# Clone Swap repository
RUN git clone https://github.com/Hackathonners/swap.git
WORKDIR swap

# Create Swap configuration file
RUN cp .env.example .env

# Install Swap composer
RUN composer install

# Install Swap npm
RUN npm install

# Generate key Swap
RUN php artisan key:generate

# Copy start script
COPY ./script.sh .

# Add execution permission to start script
RUN chmod +x script.sh

# Expose port 8000
EXPOSE 8000

# Start Swap server
#tirar partido das duas vars novas, para não fzr o migrate e seed desnecessariamente
CMD ./script.sh
```

**Refazer a secção Swap Components**
Estar dentro da pasta getting-started/app.
Verificar quais os processos a correr e remover o image_swap antigo

* sudo docker ps 
* sudo docker container rm -f <id do container id>

```
vagrant@VM1:~/app/getting-started$ docker ps -all

CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS                                                  NAMES
ef2542eab47b   mysql:latest   "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   0.0.0.0:3306->3306/tcp, :::3306->3306/tcp, 33060/tcp   db_content

```

Build da Swap Docker image.

* sudo docker build . -t image_swap

* sudo docker run --net network_ascn -p 8000:8000 --name swap -d image_swap

* Abrir no browser: 192.168.56.101:8000


**Para apagar a VM**

* exit

* vagrant destroy