import requests

class Mysql:

    def __init__(self):
        self.token = ""

    def login(self, password):
        data = {"password": password}
        response = requests.post("http://localhost:8080/api/auth/login", json=data).json()
        self.token = response["token"]

    def exec(self, query):
        headers = {"Authorization": f"Bearer {self.token}"}
        data = {"query": query}
        return requests.post("http://localhost:8080/api/mysql/exec", headers=headers, json=data).json()
    


def test_table():
    mysql = Mysql()

    # mysql.exec("create table product (id, name, price)")

    # for i in range(100):
    #     price = i * 100
    #     name = f"name{i}"
    #     query = f"insert into product (id, name, price) values ({i}, {name}, {price})"
    #     mysql.exec(query)

    select = "select id, name, price from product where id<=2"
    result = mysql.exec(select)

    print(result.json())

# test_table()

mysql = Mysql();
mysql.login("123123")
print(mysql.exec("select id, name, price from product where id<=2"))