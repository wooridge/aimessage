from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    
    # 访问GitHub页面
    page.goto('http://localhost:8080/github')
    page.wait_for_load_state('networkidle')
    
    # 点击同步按钮
    page.click('button:has-text("同步GitHub")')
    print("已点击同步按钮")
    
    # 等待同步完成（toast提示）
    page.wait_for_timeout(5000)
    
    # 截图查看结果
    page.screenshot(path='github_after_sync.png', full_page=True)
    print("同步后截图已保存到 github_after_sync.png")
    
    browser.close()
