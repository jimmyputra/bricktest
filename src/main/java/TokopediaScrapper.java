import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.PrintWriter;
import java.net.URLDecoder;

public class TokopediaScrapper {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36";
    public static WebDriver driver;
    public static final String QUERY_PAGE = "page=";
    public static final String TOKOPEDIA_URL = "https://www.tokopedia.com/p/handphone-tablet/handphone";
    public static final int MAX_ITEM = 100;

    public static void main(String[] args) throws Exception {

        Elements productNameResult;
        Elements descriptionResult;
        Elements priceResult;
        Elements ratingResult;
        Elements merchantNameResult;
        String productName;
        String descName;
        String imgLink;
        String price;
        String rating;
        String merchantName;
        ChromeOptions options =  new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("user-agent="+USER_AGENT);
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //Create CSV
        final PrintWriter out = new PrintWriter("results.csv");
        out.write("Product Name; Description; Image Link; Price; Rating; Merchant Name\n");

        driver.get(TOKOPEDIA_URL+"?"+QUERY_PAGE+"1");
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(5000);
        final Document doc1 = Jsoup.parseBodyFragment(driver.getPageSource());
        driver.get(TOKOPEDIA_URL+"?"+QUERY_PAGE+"2");
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(5000);
        final Document doc2 = Jsoup.parseBodyFragment(driver.getPageSource());
        Elements links = doc1.select("a[data-testid=lnkProductContainer]");
        Elements links2 = doc2.select("a[data-testid=lnkProductContainer]");

        for (int i=0;i<MAX_ITEM;i++) {
            driver.get(getProductHRef(links.get(i).attr("href")));
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(5000);
            Document productDetail = Jsoup.parseBodyFragment(driver.getPageSource());
            productNameResult = productDetail.select("h1[data-testid=lblPDPDetailProductName]");
            descriptionResult = productDetail.select("div[data-testid=lblPDPDescriptionProduk]");
            priceResult = productDetail.select("div[data-testid=lblPDPDetailProductPrice]");
            ratingResult = productDetail.select("span[data-testid=lblPDPDetailProductRatingNumber]");
            merchantNameResult = productDetail.select("a[data-testid=llbPDPFooterShopName]");
            productName = productNameResult.text();
            descName = descriptionResult.get(0).text();
            imgLink = driver.getCurrentUrl();
            price = priceResult.text();
            rating = ratingResult.text();
            merchantName = merchantNameResult.get(0).text();
            out.write(productName + "; " + descName + "; " + imgLink + "; " + price + "; " + rating + "; " + merchantName + "\n");

        }

        if(links.size()<MAX_ITEM)
        {
            for(int j=0;j<(MAX_ITEM-links.size());j++){
                driver.get(getProductHRef(links2.get(j).attr("href")));
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(5000);
                Document productDetail = Jsoup.parseBodyFragment(driver.getPageSource());
                productNameResult = productDetail.select("h1[data-testid=lblPDPDetailProductName]");
                descriptionResult = productDetail.select("div[data-testid=lblPDPDescriptionProduk]");
                priceResult = productDetail.select("div[data-testid=lblPDPDetailProductPrice]");
                ratingResult = productDetail.select("span[data-testid=lblPDPDetailProductRatingNumber]");
                merchantNameResult = productDetail.select("a[data-testid=llbPDPFooterShopName]");
                productName = productNameResult.text();
                descName = descriptionResult.get(0).text();
                imgLink = driver.getCurrentUrl();
                price = priceResult.text();
                rating = ratingResult.text();
                merchantName = merchantNameResult.get(0).text();
                out.write(productName + "; " + descName + "; " + imgLink + "; " + price + "; " + rating + "; " + merchantName + "\n");
            }
        }
        driver.quit();
        out.close();

    }

    private static String getProductHRef(String detailHRef) throws Exception {
        String productHRef = null;
        if (detailHRef.startsWith("https://ta")) {
            String queryString = detailHRef.substring(detailHRef.indexOf("?") + 1);
            String[] parameters = queryString.split("&");
            for (String param : parameters) {
                String[] paramKeyAndValue = param.split("=");
                if (paramKeyAndValue[0].equalsIgnoreCase("r")) {
                    productHRef = paramKeyAndValue[1];
                    productHRef = URLDecoder.decode(productHRef, "ASCII");
                    break;
                }
            }
        } else {
            productHRef = detailHRef;
        }
        return productHRef;
    }

}

