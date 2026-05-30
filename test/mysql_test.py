import requests

class Mysql:
    def exec(self, query):
        data = {"query": query}
        return requests.post("http://localhost:8080/api/mysql/exec", json=data).json()
    


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
print(mysql.exec("select id, name, price from product where id<=2"))