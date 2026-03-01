import requests

try:
    response = requests.get('http://123.57.78.157:8080/', timeout=10)
    print(f"状态码: {response.status_code}")
    print(f"响应长度: {len(response.text)}")
    if response.status_code == 200:
        print("✓ 部署成功！应用可访问")
        print("\n页面标题预览:")
        if '<title>' in response.text:
            start = response.text.find('<title>') + 7
            end = response.text.find('</title>')
            print(f"  {response.text[start:end]}")
    else:
        print(f"✗ 返回状态码: {response.status_code}")
except Exception as e:
    print(f"✗ 访问失败: {e}")

# 测试GitHub页面
try:
    response = requests.get('http://123.57.78.157:8080/github', timeout=10)
    print(f"\nGitHub页面状态码: {response.status_code}")
    if response.status_code == 200:
        print("✓ GitHub页面可访问")
except Exception as e:
    print(f"✗ GitHub页面访问失败: {e}")
