from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    
    # 访问GitHub页面
    page.goto('http://localhost:8080/github')
    page.wait_for_load_state('networkidle')
    
    # 获取页面内容
    content = page.content()
    
    # 检查是否有中文简介相关的内容
    if "简介" in content:
        print("✓ 页面包含中文简介元素")
    else:
        print("✗ 页面没有中文简介元素")
    
    if "descriptionZh" in content:
        print("✓ 页面包含descriptionZh相关内容")
    else:
        print("✗ 页面没有descriptionZh相关内容")
    
    # 检查是否有蓝色背景样式
    if "bg-blue-50" in content:
        print("✓ 页面包含蓝色背景样式")
    else:
        print("✗ 页面没有蓝色背景样式")
    
    # 统计项目卡片数量
    cards = page.locator('.bg-white.rounded-xl').count()
    print(f"项目卡片数量: {cards}")
    
    browser.close()
