package com.framework.utils;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Unit tests for DataGenerator class
 */
public class DataGeneratorTest {
    
    private DataGenerator dataGenerator;
    private DataGenerator seededGenerator;
    
    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
        seededGenerator = new DataGenerator(12345L); // Fixed seed for reproducible tests
    }
    
    @Test
    public void testConstructorWithDefaults() {
        DataGenerator generator = new DataGenerator();
        Assert.assertNotNull(generator);
        Assert.assertNotNull(generator.getFaker());
    }
    
    @Test
    public void testConstructorWithLocale() {
        DataGenerator generator = new DataGenerator(Locale.FRENCH);
        Assert.assertNotNull(generator);
        Assert.assertNotNull(generator.getFaker());
    }
    
    @Test
    public void testConstructorWithSeed() {
        DataGenerator generator = new DataGenerator(12345L);
        Assert.assertNotNull(generator);
        
        // Test reproducibility
        String name1 = generator.generateFirstName();
        DataGenerator generator2 = new DataGenerator(12345L);
        String name2 = generator2.generateFirstName();
        Assert.assertEquals(name1, name2);
    }
    
    @Test
    public void testConstructorWithLocaleAndSeed() {
        DataGenerator generator = new DataGenerator(Locale.GERMAN, 12345L);
        Assert.assertNotNull(generator);
        Assert.assertNotNull(generator.getFaker());
    }
    
    // Personal Information Tests
    
    @Test
    public void testGenerateFirstName() {
        String firstName = dataGenerator.generateFirstName();
        Assert.assertNotNull(firstName);
        Assert.assertFalse(firstName.trim().isEmpty());
    }
    
    @Test
    public void testGenerateLastName() {
        String lastName = dataGenerator.generateLastName();
        Assert.assertNotNull(lastName);
        Assert.assertFalse(lastName.trim().isEmpty());
    }
    
    @Test
    public void testGenerateFullName() {
        String fullName = dataGenerator.generateFullName();
        Assert.assertNotNull(fullName);
        Assert.assertFalse(fullName.trim().isEmpty());
        Assert.assertTrue(fullName.contains(" ")); // Should contain space between names
    }
    
    @Test
    public void testGenerateUsername() {
        String username = dataGenerator.generateUsername();
        Assert.assertNotNull(username);
        Assert.assertFalse(username.trim().isEmpty());
    }
    
    @Test
    public void testGenerateEmail() {
        String email = dataGenerator.generateEmail();
        Assert.assertNotNull(email);
        Assert.assertTrue(dataGenerator.isValidEmail(email));
    }
    
    @Test
    public void testGenerateEmailWithDomain() {
        String domain = "example.com";
        String email = dataGenerator.generateEmail(domain);
        Assert.assertNotNull(email);
        Assert.assertTrue(email.endsWith("@" + domain));
        Assert.assertTrue(dataGenerator.isValidEmail(email));
    }
    
    @Test
    public void testGeneratePhoneNumber() {
        String phoneNumber = dataGenerator.generatePhoneNumber();
        Assert.assertNotNull(phoneNumber);
        Assert.assertFalse(phoneNumber.trim().isEmpty());
    }
    
    @Test
    public void testGeneratePhoneNumberWithFormat() {
        String format = "###-###-####";
        String phoneNumber = dataGenerator.generatePhoneNumber(format);
        Assert.assertNotNull(phoneNumber);
        Assert.assertTrue(phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}"));
    }
    
    @Test
    public void testGenerateDateOfBirth() {
        LocalDate dob = dataGenerator.generateDateOfBirth(18, 65);
        Assert.assertNotNull(dob);
        
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(65);
        LocalDate maxDate = now.minusYears(18);
        
        Assert.assertTrue(dob.isAfter(minDate) || dob.isEqual(minDate));
        Assert.assertTrue(dob.isBefore(maxDate) || dob.isEqual(maxDate));
    }
    
    // Address Tests
    
    @Test
    public void testGenerateStreetAddress() {
        String address = dataGenerator.generateStreetAddress();
        Assert.assertNotNull(address);
        Assert.assertFalse(address.trim().isEmpty());
    }
    
    @Test
    public void testGenerateCity() {
        String city = dataGenerator.generateCity();
        Assert.assertNotNull(city);
        Assert.assertFalse(city.trim().isEmpty());
    }
    
    @Test
    public void testGenerateState() {
        String state = dataGenerator.generateState();
        Assert.assertNotNull(state);
        Assert.assertFalse(state.trim().isEmpty());
    }
    
    @Test
    public void testGenerateStateAbbr() {
        String stateAbbr = dataGenerator.generateStateAbbr();
        Assert.assertNotNull(stateAbbr);
        Assert.assertEquals(stateAbbr.length(), 2);
    }
    
    @Test
    public void testGenerateZipCode() {
        String zipCode = dataGenerator.generateZipCode();
        Assert.assertNotNull(zipCode);
        Assert.assertTrue(dataGenerator.isValidZipCode(zipCode));
    }
    
    @Test
    public void testGenerateCountry() {
        String country = dataGenerator.generateCountry();
        Assert.assertNotNull(country);
        Assert.assertFalse(country.trim().isEmpty());
    }
    
    @Test
    public void testGenerateCompleteAddress() {
        Map<String, String> address = dataGenerator.generateCompleteAddress();
        Assert.assertNotNull(address);
        Assert.assertTrue(address.containsKey("street"));
        Assert.assertTrue(address.containsKey("city"));
        Assert.assertTrue(address.containsKey("state"));
        Assert.assertTrue(address.containsKey("zipCode"));
        Assert.assertTrue(address.containsKey("country"));
        
        Assert.assertNotNull(address.get("street"));
        Assert.assertNotNull(address.get("city"));
        Assert.assertNotNull(address.get("state"));
        Assert.assertNotNull(address.get("zipCode"));
        Assert.assertNotNull(address.get("country"));
    }
    
    // Company and Business Tests
    
    @Test
    public void testGenerateCompanyName() {
        String companyName = dataGenerator.generateCompanyName();
        Assert.assertNotNull(companyName);
        Assert.assertFalse(companyName.trim().isEmpty());
    }
    
    @Test
    public void testGenerateJobTitle() {
        String jobTitle = dataGenerator.generateJobTitle();
        Assert.assertNotNull(jobTitle);
        Assert.assertFalse(jobTitle.trim().isEmpty());
    }
    
    @Test
    public void testGenerateDepartment() {
        String department = dataGenerator.generateDepartment();
        Assert.assertNotNull(department);
        Assert.assertFalse(department.trim().isEmpty());
    }
    
    // Internet and Technology Tests
    
    @Test
    public void testGenerateUrl() {
        String url = dataGenerator.generateUrl();
        Assert.assertNotNull(url);
        Assert.assertTrue(url.startsWith("http"));
    }
    
    @Test
    public void testGenerateDomainName() {
        String domain = dataGenerator.generateDomainName();
        Assert.assertNotNull(domain);
        Assert.assertTrue(domain.contains("."));
    }
    
    @Test
    public void testGenerateIpAddress() {
        String ip = dataGenerator.generateIpAddress();
        Assert.assertNotNull(ip);
        Assert.assertTrue(ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"));
    }
    
    @Test
    public void testGenerateMacAddress() {
        String mac = dataGenerator.generateMacAddress();
        Assert.assertNotNull(mac);
        Assert.assertTrue(mac.matches("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})"));
    }
    
    @Test
    public void testGeneratePassword() {
        String password = dataGenerator.generatePassword();
        Assert.assertNotNull(password);
        Assert.assertTrue(password.length() >= 8);
        Assert.assertTrue(password.length() <= 12);
    }
    
    @Test
    public void testGeneratePasswordWithRequirements() {
        String password = dataGenerator.generatePassword(10, 15, true, true, true, true);
        Assert.assertNotNull(password);
        Assert.assertTrue(password.length() >= 10);
        Assert.assertTrue(password.length() <= 15);
        Assert.assertTrue(dataGenerator.isValidPassword(password, 10, true, true, true, true));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGeneratePasswordNoCharacterTypes() {
        dataGenerator.generatePassword(8, 12, false, false, false, false);
    }
    
    // Financial Tests
    
    @Test
    public void testGenerateCreditCardNumber() {
        String creditCard = dataGenerator.generateCreditCardNumber();
        Assert.assertNotNull(creditCard);
        Assert.assertFalse(creditCard.trim().isEmpty());
    }
    
    @Test
    public void testGenerateIban() {
        String iban = dataGenerator.generateIban();
        Assert.assertNotNull(iban);
        Assert.assertFalse(iban.trim().isEmpty());
    }
    
    @Test
    public void testGenerateBic() {
        String bic = dataGenerator.generateBic();
        Assert.assertNotNull(bic);
        Assert.assertFalse(bic.trim().isEmpty());
    }
    
    // Numeric Tests
    
    @Test
    public void testGenerateRandomInt() {
        int randomInt = dataGenerator.generateRandomInt(1, 10);
        Assert.assertTrue(randomInt >= 1);
        Assert.assertTrue(randomInt <= 10);
    }
    
    @Test
    public void testGenerateRandomLong() {
        long randomLong = dataGenerator.generateRandomLong(100L, 200L);
        Assert.assertTrue(randomLong >= 100L);
        Assert.assertTrue(randomLong <= 200L);
    }
    
    @Test
    public void testGenerateRandomDouble() {
        double randomDouble = dataGenerator.generateRandomDouble(1.0, 2.0);
        Assert.assertTrue(randomDouble >= 1.0);
        Assert.assertTrue(randomDouble < 2.0);
    }
    
    @Test
    public void testGenerateRandomBoolean() {
        boolean randomBoolean = dataGenerator.generateRandomBoolean();
        // Just verify it returns a boolean (true or false)
        Assert.assertTrue(randomBoolean == true || randomBoolean == false);
    }
    
    // Date and Time Tests
    
    @Test
    public void testGenerateRandomDate() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        LocalDate randomDate = dataGenerator.generateRandomDate(startDate, endDate);
        
        Assert.assertNotNull(randomDate);
        Assert.assertTrue(randomDate.isAfter(startDate) || randomDate.isEqual(startDate));
        Assert.assertTrue(randomDate.isBefore(endDate) || randomDate.isEqual(endDate));
    }
    
    @Test
    public void testGeneratePastDate() {
        LocalDate pastDate = dataGenerator.generatePastDate(30);
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysAgo = now.minusDays(30);
        
        Assert.assertNotNull(pastDate);
        Assert.assertTrue(pastDate.isAfter(thirtyDaysAgo) || pastDate.isEqual(thirtyDaysAgo));
        Assert.assertTrue(pastDate.isBefore(now) || pastDate.isEqual(now));
    }
    
    @Test
    public void testGenerateFutureDate() {
        LocalDate futureDate = dataGenerator.generateFutureDate(30);
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysAhead = now.plusDays(30);
        
        Assert.assertNotNull(futureDate);
        Assert.assertTrue(futureDate.isAfter(now) || futureDate.isEqual(now));
        Assert.assertTrue(futureDate.isBefore(thirtyDaysAhead) || futureDate.isEqual(thirtyDaysAhead));
    }
    
    @Test
    public void testGenerateRandomDateTime() {
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 12, 31, 23, 59);
        LocalDateTime randomDateTime = dataGenerator.generateRandomDateTime(startDateTime, endDateTime);
        
        Assert.assertNotNull(randomDateTime);
        Assert.assertTrue(randomDateTime.isAfter(startDateTime) || randomDateTime.isEqual(startDateTime));
        Assert.assertTrue(randomDateTime.isBefore(endDateTime) || randomDateTime.isEqual(endDateTime));
    }
    
    // Text Generation Tests
    
    @Test
    public void testGenerateText() {
        String text = dataGenerator.generateText(5);
        Assert.assertNotNull(text);
        Assert.assertFalse(text.trim().isEmpty());
    }
    
    @Test
    public void testGenerateSentence() {
        String sentence = dataGenerator.generateSentence();
        Assert.assertNotNull(sentence);
        Assert.assertFalse(sentence.trim().isEmpty());
    }
    
    @Test
    public void testGenerateSentences() {
        String sentences = dataGenerator.generateSentences(3);
        Assert.assertNotNull(sentences);
        Assert.assertFalse(sentences.trim().isEmpty());
    }
    
    @Test
    public void testGenerateParagraph() {
        String paragraph = dataGenerator.generateParagraph();
        Assert.assertNotNull(paragraph);
        Assert.assertFalse(paragraph.trim().isEmpty());
    }
    
    // Collection Utility Tests
    
    @Test
    public void testSelectRandomFromArray() {
        String[] options = {"Option1", "Option2", "Option3"};
        String selected = dataGenerator.selectRandom(options);
        Assert.assertNotNull(selected);
        Assert.assertTrue(Arrays.asList(options).contains(selected));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSelectRandomFromEmptyArray() {
        String[] emptyArray = {};
        dataGenerator.selectRandom(emptyArray);
    }
    
    @Test
    public void testSelectRandomFromList() {
        List<String> options = Arrays.asList("Option1", "Option2", "Option3");
        String selected = dataGenerator.selectRandom(options);
        Assert.assertNotNull(selected);
        Assert.assertTrue(options.contains(selected));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSelectRandomFromEmptyList() {
        List<String> emptyList = Arrays.asList();
        dataGenerator.selectRandom(emptyList);
    }
    
    @Test
    public void testSelectRandomElements() {
        List<String> options = Arrays.asList("A", "B", "C", "D", "E");
        List<String> selected = dataGenerator.selectRandomElements(options, 3);
        
        Assert.assertNotNull(selected);
        Assert.assertEquals(selected.size(), 3);
        
        // Verify all selected elements are from original list
        for (String element : selected) {
            Assert.assertTrue(options.contains(element));
        }
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSelectRandomElementsCountTooLarge() {
        List<String> options = Arrays.asList("A", "B");
        dataGenerator.selectRandomElements(options, 5);
    }
    
    // Validation Tests
    
    @Test
    public void testIsValidEmail() {
        Assert.assertTrue(dataGenerator.isValidEmail("test@example.com"));
        Assert.assertTrue(dataGenerator.isValidEmail("user.name@domain.co.uk"));
        Assert.assertFalse(dataGenerator.isValidEmail("invalid-email"));
        Assert.assertFalse(dataGenerator.isValidEmail("@domain.com"));
        Assert.assertFalse(dataGenerator.isValidEmail("user@"));
    }
    
    @Test
    public void testIsValidPhoneNumber() {
        Assert.assertTrue(dataGenerator.isValidPhoneNumber("123-456-7890"));
        Assert.assertTrue(dataGenerator.isValidPhoneNumber("(123) 456-7890"));
        Assert.assertTrue(dataGenerator.isValidPhoneNumber("123.456.7890"));
        Assert.assertTrue(dataGenerator.isValidPhoneNumber("1234567890"));
        Assert.assertFalse(dataGenerator.isValidPhoneNumber("123-456"));
        Assert.assertFalse(dataGenerator.isValidPhoneNumber("abc-def-ghij"));
    }
    
    @Test
    public void testIsValidZipCode() {
        Assert.assertTrue(dataGenerator.isValidZipCode("12345"));
        Assert.assertTrue(dataGenerator.isValidZipCode("12345-6789"));
        Assert.assertFalse(dataGenerator.isValidZipCode("1234"));
        Assert.assertFalse(dataGenerator.isValidZipCode("123456"));
        Assert.assertFalse(dataGenerator.isValidZipCode("abcde"));
    }
    
    @Test
    public void testIsValidCreditCardNumber() {
        // Valid test credit card numbers (Luhn algorithm compliant)
        Assert.assertTrue(dataGenerator.isValidCreditCardNumber("4532015112830366"));
        Assert.assertTrue(dataGenerator.isValidCreditCardNumber("4532-0151-1283-0366"));
        Assert.assertTrue(dataGenerator.isValidCreditCardNumber("4532 0151 1283 0366"));
        
        Assert.assertFalse(dataGenerator.isValidCreditCardNumber("1234567890123456"));
        Assert.assertFalse(dataGenerator.isValidCreditCardNumber("abcd-efgh-ijkl-mnop"));
        Assert.assertFalse(dataGenerator.isValidCreditCardNumber(""));
    }
    
    @Test
    public void testIsValidPassword() {
        Assert.assertTrue(dataGenerator.isValidPassword("Password123!", 8, true, true, true, true));
        Assert.assertTrue(dataGenerator.isValidPassword("password123", 8, false, true, true, false));
        
        Assert.assertFalse(dataGenerator.isValidPassword("short", 8, false, false, false, false));
        Assert.assertFalse(dataGenerator.isValidPassword("nouppercase123!", 8, true, true, true, true));
        Assert.assertFalse(dataGenerator.isValidPassword("NOLOWERCASE123!", 8, true, true, true, true));
        Assert.assertFalse(dataGenerator.isValidPassword("NoNumbers!", 8, true, true, true, true));
        Assert.assertFalse(dataGenerator.isValidPassword("NoSpecialChars123", 8, true, true, true, true));
    }
    
    @Test
    public void testFormatDate() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        String formatted = dataGenerator.formatDate(date, "yyyy-MM-dd");
        Assert.assertEquals(formatted, "2023-12-25");
        
        String formatted2 = dataGenerator.formatDate(date, "MM/dd/yyyy");
        Assert.assertEquals(formatted2, "12/25/2023");
    }
    
    @Test
    public void testFormatDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 14, 30, 45);
        String formatted = dataGenerator.formatDateTime(dateTime, "yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(formatted, "2023-12-25 14:30:45");
    }
    
    @Test
    public void testGetFaker() {
        Assert.assertNotNull(dataGenerator.getFaker());
    }
}