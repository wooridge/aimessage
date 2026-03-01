import requests
import time

# 强制重新生成所有中文描述
print("强制重新生成所有中文描述...")
response = requests.post('http://localhost:8080/api/sync/github/regenerate-descriptions')
print(f"结果: {response.text}")

# 等待
print("\n等待2秒...")
time.sleep(2)

# 检查更新后的项目
print("\n检查更新后的中文描述...")
response = requests.get('http://localhost:8080/api/github/projects')
projects = response.json()

print(f"\n获取到 {len(projects)} 个项目")
print("\n前12个项目的中文描述:")
print("=" * 80)
for i, project in enumerate(projects[:12]):
    print(f"\n{i+1}. {project.get('name')} ({project.get('language')})")
    print(f"   原描述: {project.get('description', 'N/A')[:65]}...")
    print(f"   中文简介: {project.get('descriptionZh', 'N/A')}")
    print("-" * 80)
