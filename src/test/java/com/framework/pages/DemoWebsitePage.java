package com.framework.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for The Internet Demo Website (https://the-internet.herokuapp.com)
 * Demonstrates various web testing scenarios
 */
public class DemoWebsitePage extends BasePage {

    // Homepage elements
    @FindBy(tagName = "h1")
    private WebElement pageTitle;

    @FindBy(tagName = "h2")
    private WebElement subtitle;

    @FindBy(linkText = "Form Authentication")
    private WebElement formAuthLink;

    @FindBy(linkText = "Checkboxes")
    private WebElement checkboxesLink;

    @FindBy(linkText = "Dropdown")
    private WebElement dropdownLink;

    @FindBy(linkText = "Dynamic Loading")
    private WebElement dynamicLoadingLink;

    @FindBy(linkText = "File Upload")
    private WebElement fileUploadLink;

    @FindBy(linkText = "JavaScript Alerts")
    private WebElement jsAlertsLink;

    // Form Authentication page elements
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(id = "flash")
    private WebElement flashMessage;

    @FindBy(css = "a[href='/logout']")
    private WebElement logoutLink;

    // Checkboxes page elements
    @FindBy(css = "input[type='checkbox']:nth-of-type(1)")
    private WebElement checkbox1;

    @FindBy(css = "input[type='checkbox']:nth-of-type(2)")
    private WebElement checkbox2;

    // Dropdown page elements
    @FindBy(id = "dropdown")
    private WebElement dropdownSelect;

    // Dynamic Loading elements
    @FindBy(css = "button")
    private WebElement startButton;

    @FindBy(id = "loading")
    private WebElement loadingIndicator;

    @FindBy(id = "finish")
    private WebElement finishMessage;

    // JavaScript Alerts elements
    @FindBy(css = "button[onclick='jsAlert()']")
    private WebElement jsAlertButton;

    @FindBy(css = "button[onclick='jsConfirm()']")
    private WebElement jsConfirmButton;

    @FindBy(css = "button[onclick='jsPrompt()']")
    private WebElement jsPromptButton;

    @FindBy(id = "result")
    private WebElement alertResult;

    public DemoWebsitePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Homepage methods
    public boolean isHomepageLoaded() {
        return isElementDisplayed(pageTitle) &&
               pageTitle.getText().contains("Welcome to the-internet");
    }

    public String getPageTitle() {
        return isElementDisplayed(pageTitle) ? pageTitle.getText() : "";
    }

    public void clickFormAuthentication() {
        clickElement(formAuthLink);
    }

    public void clickCheckboxes() {
        clickElement(checkboxesLink);
    }

    public void clickDropdown() {
        clickElement(dropdownLink);
    }

    public void clickDynamicLoading() {
        clickElement(dynamicLoadingLink);
    }

    public void clickJavaScriptAlerts() {
        clickElement(jsAlertsLink);
    }

    // Form Authentication methods
    public void login(String username, String password) {
        typeText(usernameField, username);
        typeText(passwordField, password);
        clickElement(loginButton);
    }

    public boolean isLoginSuccessful() {
        return isElementDisplayed(flashMessage) &&
               flashMessage.getText().contains("You logged into a secure area!");
    }

    public boolean isLoginFailed() {
        return isElementDisplayed(flashMessage) &&
               flashMessage.getText().contains("Your username is invalid!");
    }

    public void logout() {
        if (isElementDisplayed(logoutLink)) {
            clickElement(logoutLink);
        }
    }

    public boolean isLoggedOut() {
        return isElementDisplayed(flashMessage) &&
               flashMessage.getText().contains("You logged out of the secure area!");
    }

    // Checkboxes methods
    public void toggleCheckbox1() {
        clickElement(checkbox1);
    }

    public void toggleCheckbox2() {
        clickElement(checkbox2);
    }

    public boolean isCheckbox1Selected() {
        return checkbox1.isSelected();
    }

    public boolean isCheckbox2Selected() {
        return checkbox2.isSelected();
    }

    // Dropdown methods
    public void selectDropdownOption(String optionText) {
        selectDropdownByVisibleText(dropdownSelect, optionText);
    }

    public String getSelectedDropdownOption() {
        return getSelectedDropdownText(dropdownSelect);
    }

    // Dynamic Loading methods
    public void clickStart() {
        clickElement(startButton);
    }

    public boolean isLoadingDisplayed() {
        return isElementDisplayed(loadingIndicator);
    }

    public boolean isFinishMessageDisplayed() {
        return isElementDisplayed(finishMessage);
    }

    public String getFinishMessage() {
        return isElementDisplayed(finishMessage) ? finishMessage.getText() : "";
    }

    // JavaScript Alerts methods
    public void clickJsAlert() {
        clickElement(jsAlertButton);
    }

    public void clickJsConfirm() {
        clickElement(jsConfirmButton);
    }

    public void clickJsPrompt() {
        clickElement(jsPromptButton);
    }

    public String getAlertResult() {
        return isElementDisplayed(alertResult) ? alertResult.getText() : "";
    }

    // Alert handling methods
    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    public void dismissAlert() {
        driver.switchTo().alert().dismiss();
    }

    public void sendTextToAlert(String text) {
        driver.switchTo().alert().sendKeys(text);
        driver.switchTo().alert().accept();
    }

    public String getAlertText() {
        return driver.switchTo().alert().getText();
    }
}