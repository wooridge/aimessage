from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    
    # 访问GitHub页面
    page.goto('http://localhost:8080/github')
    page.wait_for_load_state('networkidle')
    
    # 截图查看结果
    page.screenshot(path='github_improved.png', full_page=True)
    print("截图已保存到 github_improved.png")
    
    # 获取页面内容检查
    content = page.content()
    
    # 检查一些关键描述是否在页面上
    key_descriptions = [
        "Python包管理工具",
        "AI编码助手",
        "终端模拟器",
        "零知识证明协议",
        "代码编辑器",
        "跨平台桌面应用框架",
        "JavaScript/TypeScript运行时环境",
        "远程桌面应用"
    ]
    
    print("\n检查关键描述是否显示:")
    for desc in key_descriptions:
        if desc in content:
            print(f"  ✓ {desc}")
        else:
            print(f"  ✗ {desc} (未找到)")
    
    browser.close()
