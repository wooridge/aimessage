import requests
import socket

# 测试域名解析
try:
    ip = socket.gethostbyname('wangyurenpractice.xyz')
    print(f"域名解析: wangyurenpractice.xyz -> {ip}")
    if ip == '123.57.78.157':
        print("✓ 域名解析正确")
    else:
        print(f"✗ 域名解析错误，期望 123.57.78.157，实际 {ip}")
except Exception as e:
    print(f"✗ 域名解析失败: {e}")

# 测试HTTP访问
print("\n测试HTTP访问...")
try:
    response = requests.get('http://wangyurenpractice.xyz/', timeout=10)
    print(f"状态码: {response.status_code}")
    if response.status_code == 200:
        print("✓ 域名访问成功！")
        if '<title>' in response.text:
            start = response.text.find('<title>') + 7
            end = response.text.find('</title>')
            print(f"页面标题: {response.text[start:end]}")
    else:
        print(f"✗ 返回状态码: {response.status_code}")
except Exception as e:
    print(f"✗ 访问失败: {e}")

# 测试www子域名
print("\n测试www子域名...")
try:
    response = requests.get('http://www.wangyurenpractice.xyz/', timeout=10)
    print(f"状态码: {response.status_code}")
    if response.status_code == 200:
        print("✓ www子域名访问成功！")
except Exception as e:
    print(f"✗ 访问失败: {e}")
