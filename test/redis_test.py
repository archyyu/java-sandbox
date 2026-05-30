import requests

class Redis:

    def exec(self, query):
        data = {"command": query}
        return requests.post("http://localhost:8080/api/redis/exec", json=data).json()

    def set(self, key, value):
        query = f"SET {key} {value}"
        return self.exec(query)

    def get(self, key):
        query = f"GET {key}"
        return self.exec(query)

    def set_list(self, key, list):
        query = f"LPUSH {key} {' '.join(str(x) for x in list)}"
        return self.exec(query)
    
    def get_list(self, key, left, right):
        query = f"LRANGE {key} {left} {right}"
        return self.exec(query)

    def add_set(self, key, set):
        query = f"SADD {key} {' '.join(str(x) for x in set)}"
        return self.exec(query)
    
    def members_set(self, key):
        query = f"SMEMBER {key}"
        return self.exec(query)

    def map_set(self, key, subkey, subvalue):
        query = f"HSET {key} {subkey} {subvalue}"
        return self.exec(query)

    def map_get(self, key, subkey):
        query = f"HGET {key} {subkey}"
        return self.exec(query)


redis = Redis()


print(redis.get("key1"))
print(redis.get("key2"))