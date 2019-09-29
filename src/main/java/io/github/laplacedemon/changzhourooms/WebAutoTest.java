package io.github.laplacedemon.changzhourooms;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebAutoTest {
    static WebDriver driver;
    static String baseUrl = "https://house.hualongxiang.com/plot";
    
    static{
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\zhuoyun\\Desktop\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
    }
    
    static List<String> roomLinksList = new ArrayList<>();
    
    public static void main(String[] args) throws InterruptedException, IOException {
        driver.get(baseUrl);
        List<WebElement> pageBoxAList = driver.findElements(By.cssSelector(".page-box a"));
        WebElement pageBoxLastA = pageBoxAList.get(pageBoxAList.size()-1);
        String pageBoxLastAHrefValue = pageBoxLastA.getAttribute("href");
        System.out.println("末页链接：" + pageBoxLastAHrefValue);
        String lastPageNo = queryParam(pageBoxLastAHrefValue,"page");
        
        for (int i = 1; i<=Integer.valueOf(lastPageNo); i++) {
            Thread.sleep(500);
            driver.get(baseUrl + "?page=" + i);
            // 查找链接地址
            List<WebElement> aWebElementList = driver.findElements(By.cssSelector(".item-list li div.pic.fl a"));
            for(WebElement a : aWebElementList) {
                String hrefValue = a.getAttribute("href");
                System.out.println(hrefValue);
                roomLinksList.add(hrefValue);
            }
        }
        
        File file = new File("C:\\Users\\zhuoyun\\Desktop\\rooms.txt");
        FileWriter fileWriter = new FileWriter(file);
        
        // 抓取每个房产的信息
        for (String roomLink : roomLinksList) {
            Thread.sleep(500);
            fetchRoomInfo(roomLink, fileWriter);
        }
        
//        fetchRoomInfo("https://house.hualongxiang.com/xingyunchengwx.html", fileWriter);
        
        driver.quit();
    }
    
    static void fetchRoomInfo(String roomLink, FileWriter fileWriter) throws IOException {
        driver.get(roomLink);
        WebElement h1WebElement = driver.findElement(By.cssSelector(".hj-container.hj-container-md.content h1"));
        String 地产名称 = h1WebElement.getText();
        WebElement spanWebElement = driver.findElement(By.cssSelector(".plot-info .items .first-dd span"));
        String 在售均价 = spanWebElement.getText();
        WebElement itemsWebElement = driver.findElements(By.cssSelector(".plot-info .items")).get(4);
        WebElement ddWebElement = itemsWebElement.findElement(By.cssSelector("dd"));
        String 开发企业 = ddWebElement.getText();
        WebElement mapboxWebElement = driver.findElement(By.cssSelector(".map-box"));
        String lng = mapboxWebElement.getAttribute("data-lng");
        String lat = mapboxWebElement.getAttribute("data-lat");
        String addr = mapboxWebElement.getAttribute("data-plot-addr");
//        System.out.println(
//                "地产名称：" + 地产名称 + 
//                "，在售均价：" + 在售均价 + 
//                "，开发企业：" + 开发企业 + 
//                "，地址：" + addr +
//                "，地图位置：(" + lng + "," + lat + ")");
        List<String> infoList = new ArrayList<>();
        infoList.add(地产名称);
        infoList.add(在售均价);
        infoList.add(开发企业);
        infoList.add(addr);
        infoList.add(lng);
        infoList.add(lat);
        infoList.add(roomLink);
        String csvInfo = StringUtils.join(infoList.toArray(new String[0]), ",");
        System.out.println(csvInfo);
//        byte[] bytes = csvInfo.getBytes();
//        String csvInfoGB2312 = new String(bytes, "GB2312");
        writeCSVFile(fileWriter, csvInfo);
    }
    
    static String queryParam(String url, String queryString) {
        String[] urlsearch = url.split("\\?");
        String query = urlsearch[1];
        String[] vars = query.split("&");
        
        for (int i=0; i<vars.length; i++) {
            String[] pair = vars[i].split("=");
            if(pair[0].equals(queryString)){
                return pair[1];
            }
        }
        
        return null;
    }
    
    public static void writeCSVFile(FileWriter bw, String text) throws IOException {
        bw.append(text);
        bw.append('\n');
        bw.flush();
    }
}
