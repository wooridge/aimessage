import requests
import time

# 触发同步
print("触发GitHub同步...")
response = requests.post('http://localhost:8080/api/sync/github')
print(f"同步结果: {response.text}")

# 等待同步完成
print("\n等待3秒后检查新数据...")
time.sleep(3)

# 获取更新后的项目
response = requests.get('http://localhost:8080/api/github/projects')
projects = response.json()

print(f"\n获取到 {len(projects)} 个项目")
print("\n检查是否有中文描述:")
has_zh = 0
for project in projects:
    if project.get('descriptionZh'):
        has_zh += 1
        if has_zh <= 3:
            print(f"\n项目: {project.get('name')}")
            print(f"  中文描述: {project.get('descriptionZh')}")

print(f"\n共有 {has_zh}/{len(projects)} 个项目有中文描述")
