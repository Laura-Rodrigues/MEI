def parimpar (int):
    if(int%2 == 0): 
        print("É par")
    else:
        print("É ímpar")


def media(lista):
    sum = 0
    for i in lista:
        sum += i
    result = sum /len(lista)
    print(f"\nA média é: {result}")

class Person:
    
    def __init__(self, primeiro, ultimo, idade, nacionalidade):
        self.primeiro = primeiro
        self.ultimo = ultimo
        self.idade = idade
        self.nacionalidade = nacionalidade

    def printfname(self):
        print(f"\nO primeiro nome é {self.primeiro}.")

class Student(Person):

    def __init__(self, primeiro, ultimo, idade, nacionalidade, curso, ano):
        super().__init__(primeiro, ultimo, idade, nacionalidade)
        self.curso = curso
        self.ano = ano


def chave_dic(chave):
    

if __name__ == '__main__': 
    parimpar(2)
    parimpar(5)

    lista = [1,2,3,4]
    media(lista)

    a = Person("Gisela", "Nunes", 44, "portuguesa")
    a.printfname()

    b = Student("Laura", "Rodrigues", 21, "portuguesa", "MEI", 4)
    b.printfname()

    phone_book = {
        'John' : [ '8592970000', '' ],
        'Bob' : [ '7994880000', '' ],
        'Tom' : [ '9749552647', '' ]
    }
