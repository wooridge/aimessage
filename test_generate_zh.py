import requests
import time

# 调用生成中文描述的API
print("正在生成中文描述...")
response = requests.post('http://localhost:8080/api/sync/github/generate-descriptions')
print(f"结果: {response.text}")

# 等待一下
print("\n等待2秒后刷新页面...")
time.sleep(2)

# 获取更新后的项目
response = requests.get('http://localhost:8080/api/github/projects')
projects = response.json()

print(f"\n获取到 {len(projects)} 个项目")
print("\n检查前5个项目的中文描述:")
for i, project in enumerate(projects[:5]):
    print(f"\n项目 {i+1}: {project.get('name')}")
    print(f"  中文描述: {project.get('descriptionZh', 'N/A')}")
