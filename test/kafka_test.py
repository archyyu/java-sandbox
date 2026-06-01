import requests


class Kafka:
    def __init__(self, topic):
        self.topic = topic
        self.token = ""

    def login(self, username, password):
        data = {"username": username,"password": password}
        response = requests.post("http://localhost:8080/api/auth/login", json=data).json()
        self.token = response["token"]
        print(self.token)
    
    def produce(self, dict):
        headers = {"Authorization": f"Bearer {self.token}"}
        response = requests.post(f"http://localhost:8080/api/kafka/{self.topic}/produce", headers=headers, json=dict)
        print(response)
    
    def consume(self, consumer):
        headers = {"Authorization": f"Bearer {self.token}"}
        response = requests.post(f"http://localhost:8080/api/kafka/{self.topic}/{consumer}", headers=headers).json()
        print(response);


kafka = Kafka("kafkapython")

kafka.login("user1", "pass1")

obj = {}
obj["name"] = "name"
obj["content"] = "content"

kafka.produce(obj)

kafka.consume("consumer")