package com.utils;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.Map;
import java.util.Set;

public class CookieManager {

    private static final String COOKIE_FILE = "cookies.data";
    private static final String LOCAL_STORAGE_FILE = "localStorage.data";
    private static final String SESSION_STORAGE_FILE = "sessionStorage.data";

    /** Clear browser data to avoid old users popping up */
    public static void clearBrowserData(WebDriver driver) {
        driver.manage().deleteAllCookies();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.localStorage.clear();");
        js.executeScript("window.sessionStorage.clear();");
        System.out.println("✅ Cleared cookies, localStorage, and sessionStorage");
    }

    /** Save cookies, localStorage, and sessionStorage for your preferred user */
    public static void save(WebDriver driver) {
        saveCookies(driver);
        saveLocalStorage(driver);
        saveSessionStorage(driver);
    }

    public static void saveCookies(WebDriver driver) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COOKIE_FILE))) {
            Set<Cookie> cookies = driver.manage().getCookies();
            oos.writeObject(cookies);
            System.out.println("✅ Cookies saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveLocalStorage(WebDriver driver) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOCAL_STORAGE_FILE))) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Map<String, String> localStorageMap = (Map<String, String>) js.executeScript(
                    "let items={}; for(let i=0;i<localStorage.length;i++){let key=localStorage.key(i);items[key]=localStorage.getItem(key);} return items;"
            );
            oos.writeObject(localStorageMap);
            System.out.println("✅ LocalStorage saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSessionStorage(WebDriver driver) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_STORAGE_FILE))) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Map<String, String> sessionStorageMap = (Map<String, String>) js.executeScript(
                    "let items={}; for(let i=0;i<sessionStorage.length;i++){let key=sessionStorage.key(i);items[key]=sessionStorage.getItem(key);} return items;"
            );
            oos.writeObject(sessionStorageMap);
            System.out.println("✅ SessionStorage saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Load cookies/localStorage/sessionStorage for the preferred user */
    @SuppressWarnings("unchecked")
    public static boolean load(WebDriver driver) {
        try {
            loadCookies(driver);
            loadLocalStorage(driver);
            loadSessionStorage(driver);
            driver.navigate().refresh(); // Apply everything
            System.out.println("✅ Cookies and storage loaded successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load cookies/storage. Login required.");
            return false;
        }
    }

    public static void loadCookies(WebDriver driver) throws Exception {
        File file = new File(COOKIE_FILE);
        if (!file.exists()) return;
        Set<Cookie> cookies = (Set<Cookie>) new ObjectInputStream(new FileInputStream(file)).readObject();
        for (Cookie cookie : cookies) driver.manage().addCookie(cookie);
    }

    public static void loadLocalStorage(WebDriver driver) throws Exception {
        File file = new File(LOCAL_STORAGE_FILE);
        if (!file.exists()) return;
        Map<String, String> map = (Map<String, String>) new ObjectInputStream(new FileInputStream(file)).readObject();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (Map.Entry<String, String> e : map.entrySet()) {
            js.executeScript(String.format("window.localStorage.setItem('%s','%s');", e.getKey(), e.getValue()));
        }
    }

    public static void loadSessionStorage(WebDriver driver) throws Exception {
        File file = new File(SESSION_STORAGE_FILE);
        if (!file.exists()) return;
        Map<String, String> map = (Map<String, String>) new ObjectInputStream(new FileInputStream(file)).readObject();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (Map.Entry<String, String> e : map.entrySet()) {
            js.executeScript(String.format("window.sessionStorage.setItem('%s','%s');", e.getKey(), e.getValue()));
        }
    }
}
