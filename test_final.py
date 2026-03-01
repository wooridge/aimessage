from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    
    # 访问GitHub页面
    page.goto('http://localhost:8080/github')
    page.wait_for_load_state('networkidle')
    
    # 截图查看结果
    page.screenshot(path='github_final.png', full_page=True)
    print("最终截图已保存到 github_final.png")
    
    # 检查是否有中文简介显示
    content = page.content()
    if "💡 简介：" in content:
        print("✓ 页面显示中文简介")
    else:
        print("✗ 页面没有显示中文简介")
    
    # 统计有多少个蓝色简介框
    blue_boxes = page.locator('.bg-blue-50').count()
    print(f"中文简介框数量: {blue_boxes}")
    
    browser.close()
