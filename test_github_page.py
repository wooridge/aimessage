from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    
    # 访问GitHub页面
    page.goto('http://localhost:8080/github')
    page.wait_for_load_state('networkidle')
    
    # 截图查看页面
    page.screenshot(path='github_page.png', full_page=True)
    print("截图已保存到 github_page.png")
    
    # 检查页面标题
    title = page.title()
    print(f"页面标题: {title}")
    
    # 检查是否有错误信息
    content = page.content()
    if "error" in content.lower() or "exception" in content.lower():
        print("页面可能包含错误信息")
        print(content[:1000])
    else:
        print("页面加载正常")
    
    browser.close()
