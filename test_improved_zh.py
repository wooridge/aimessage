import requests
import time

# 1. 清空现有的中文描述，重新生成
print("步骤1: 重新生成中文描述...")
response = requests.post('http://localhost:8080/api/sync/github/generate-descriptions')
print(f"结果: {response.text}")

# 等待一下
print("\n等待2秒...")
time.sleep(2)

# 2. 获取更新后的项目
print("\n步骤2: 检查更新后的中文描述...")
response = requests.get('http://localhost:8080/api/github/projects')
projects = response.json()

print(f"\n获取到 {len(projects)} 个项目")
print("\n前10个项目的中文描述:")
print("=" * 80)
for i, project in enumerate(projects[:10]):
    print(f"\n{i+1}. {project.get('name')} ({project.get('language')})")
    print(f"   原描述: {project.get('description', 'N/A')[:60]}...")
    print(f"   中文简介: {project.get('descriptionZh', 'N/A')}")
    print("-" * 80)
