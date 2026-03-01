import requests
import json

# 调用API获取GitHub项目
response = requests.get('http://localhost:8080/api/github/projects')
projects = response.json()

print(f"获取到 {len(projects)} 个项目")
print("\n前3个项目的信息:")
for i, project in enumerate(projects[:3]):
    print(f"\n项目 {i+1}:")
    print(f"  名称: {project.get('name')}")
    print(f"  描述: {project.get('description', 'N/A')[:80]}..." if project.get('description') else "  描述: N/A")
    print(f"  中文描述: {project.get('descriptionZh', 'N/A')}")
    print(f"  语言: {project.get('language')}")
