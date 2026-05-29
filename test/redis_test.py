import requests

class Redis:

    def exec(self, query):
        data = {"command": query}
        return requests.post("http://localhost:8080/api/redis/exec", json=data)

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

def redis(data):
    return requests.post("http://localhost:8080/api/redis/exec", json=data)

def rem_key(key):
    request = {"command": f"DEL {key}"}
    return redis(request)

def testKeyValue(key, value):
    rem_key(key)
    data = {"command": f"SET {key} {value}"}
    redis(data)
    data = {"command": f"GET {key}"}

    response = redis(data)
    print(response.json()["result"] == value)


testKeyValue("key1", "value1")
testKeyValue("key2", "value2")


def testKeyList(key, list):
    rem_key(key)

    data = {"command": f"LPUSH {key} {' '.join(str(x) for x in list)}"}
    redis(data)

    request = {"command": f"LRANGE {key} 0 {len(list)}"}
    response = redis(request)

    print(response.json()["result"] == [str(x) for x in list])


testKeyList("list", [1,2,3,4])
testKeyList("list2", [1,2,3,4,4,5,9])

def testKeySet(key, set):
    rem_key(key)
    request = {"command": f"SADD {key} {' '.join(str(x) for x in set)}"}
    redis(request)

    request["command"] = f"SMEMBERS {key}"
    response = redis(request)

    print(response.json()["result"] == [str(x) for x in set])

testKeySet("set", {1, 2, 3, 5})


