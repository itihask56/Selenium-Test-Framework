package com.framework.utils;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * DataGenerator provides fake data generation using JavaFaker
 * Includes random test data creation and data validation utilities
 */
public class DataGenerator {
    
    private final Faker faker;
    private final Random random;
    
    /**
     * Constructor with default locale
     */
    public DataGenerator() {
        this.faker = new Faker();
        this.random = new Random();
    }
    
    /**
     * Constructor with specific locale
     * @param locale locale for data generation
     */
    public DataGenerator(Locale locale) {
        this.faker = new Faker(locale);
        this.random = new Random();
    }
    
    /**
     * Constructor with seed for reproducible data
     * @param seed random seed
     */
    public DataGenerator(long seed) {
        this.faker = new Faker(new Random(seed));
        this.random = new Random(seed);
    }
    
    /**
     * Constructor with locale and seed
     * @param locale locale for data generation
     * @param seed random seed
     */
    public DataGenerator(Locale locale, long seed) {
        this.faker = new Faker(locale, new Random(seed));
        this.random = new Random(seed);
    }
    
    // Personal Information Generation
    
    /**
     * Generates a random first name
     * @return random first name
     */
    public String generateFirstName() {
        return faker.name().firstName();
    }
    
    /**
     * Generates a random last name
     * @return random last name
     */
    public String generateLastName() {
        return faker.name().lastName();
    }
    
    /**
     * Generates a random full name
     * @return random full name
     */
    public String generateFullName() {
        return faker.name().fullName();
    }
    
    /**
     * Generates a random username
     * @return random username
     */
    public String generateUsername() {
        return faker.name().username();
    }
    
    /**
     * Generates a random email address
     * @return random email address
     */
    public String generateEmail() {
        return faker.internet().emailAddress();
    }
    
    /**
     * Generates a random email with specific domain
     * @param domain email domain
     * @return random email with specified domain
     */
    public String generateEmail(String domain) {
        return faker.name().username() + "@" + domain;
    }
    
    /**
     * Generates a random phone number
     * @return random phone number
     */
    public String generatePhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }
    
    /**
     * Generates a random phone number with specific format
     * @param format phone number format (e.g., "###-###-####")
     */
    public String generatePhoneNumber(String format) {
        return faker.numerify(format);
    }
    
    /**
     * Generates a random date of birth
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return random date of birth
     */
    public LocalDate generateDateOfBirth(int minAge, int maxAge) {
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(maxAge);
        LocalDate maxDate = now.minusYears(minAge);
        
        long minDay = minDate.toEpochDay();
        long maxDay = maxDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay + 1);
        
        return LocalDate.ofEpochDay(randomDay);
    }
    
    // Address Generation
    
    /**
     * Generates a random street address
     * @return random street address
     */
    public String generateStreetAddress() {
        return faker.address().streetAddress();
    }
    
    /**
     * Generates a random city
     * @return random city
     */
    public String generateCity() {
        return faker.address().city();
    }
    
    /**
     * Generates a random state
     * @return random state
     */
    public String generateState() {
        return faker.address().state();
    }
    
    /**
     * Generates a random state abbreviation
     * @return random state abbreviation
     */
    public String generateStateAbbr() {
        return faker.address().stateAbbr();
    }
    
    /**
     * Generates a random ZIP code
     * @return random ZIP code
     */
    public String generateZipCode() {
        return faker.address().zipCode();
    }
    
    /**
     * Generates a random country
     * @return random country
     */
    public String generateCountry() {
        return faker.address().country();
    }
    
    /**
     * Generates a complete random address
     * @return map containing address components
     */
    public Map<String, String> generateCompleteAddress() {
        Map<String, String> address = new HashMap<>();
        address.put("street", generateStreetAddress());
        address.put("city", generateCity());
        address.put("state", generateState());
        address.put("zipCode", generateZipCode());
        address.put("country", generateCountry());
        return address;
    }
    
    // Company and Business Data
    
    /**
     * Generates a random company name
     * @return random company name
     */
    public String generateCompanyName() {
        return faker.company().name();
    }
    
    /**
     * Generates a random job title
     * @return random job title
     */
    public String generateJobTitle() {
        return faker.job().title();
    }
    
    /**
     * Generates a random department
     * @return random department
     */
    public String generateDepartment() {
        return faker.commerce().department();
    }
    
    // Internet and Technology Data
    
    /**
     * Generates a random URL
     * @return random URL
     */
    public String generateUrl() {
        return faker.internet().url();
    }
    
    /**
     * Generates a random domain name
     * @return random domain name
     */
    public String generateDomainName() {
        return faker.internet().domainName();
    }
    
    /**
     * Generates a random IP address
     * @return random IP address
     */
    public String generateIpAddress() {
        return faker.internet().ipV4Address();
    }
    
    /**
     * Generates a random MAC address
     * @return random MAC address
     */
    public String generateMacAddress() {
        return faker.internet().macAddress();
    }
    
    /**
     * Generates a random password
     * @param minLength minimum password length
     * @param maxLength maximum password length
     * @param includeUppercase include uppercase letters
     * @param includeLowercase include lowercase letters
     * @param includeNumbers include numbers
     * @param includeSpecialChars include special characters
     * @return random password
     */
    public String generatePassword(int minLength, int maxLength, boolean includeUppercase, 
                                 boolean includeLowercase, boolean includeNumbers, 
                                 boolean includeSpecialChars) {
        StringBuilder charset = new StringBuilder();
        
        if (includeUppercase) charset.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        if (includeLowercase) charset.append("abcdefghijklmnopqrstuvwxyz");
        if (includeNumbers) charset.append("0123456789");
        if (includeSpecialChars) charset.append("!@#$%^&*()_+-=[]{}|;:,.<>?");
        
        if (charset.length() == 0) {
            throw new IllegalArgumentException("At least one character type must be included");
        }
        
        int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charset.length());
            password.append(charset.charAt(randomIndex));
        }
        
        return password.toString();
    }
    
    /**
     * Generates a simple random password
     * @return random password with default settings
     */
    public String generatePassword() {
        return generatePassword(8, 12, true, true, true, false);
    }
    
    // Financial Data
    
    /**
     * Generates a random credit card number
     * @return random credit card number
     */
    public String generateCreditCardNumber() {
        return faker.finance().creditCard();
    }
    
    /**
     * Generates a random IBAN
     * @return random IBAN
     */
    public String generateIban() {
        return faker.finance().iban();
    }
    
    /**
     * Generates a random BIC
     * @return random BIC
     */
    public String generateBic() {
        return faker.finance().bic();
    }
    
    // Numeric Data Generation
    
    /**
     * Generates a random integer within range
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return random integer
     */
    public int generateRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
    /**
     * Generates a random long within range
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return random long
     */
    public long generateRandomLong(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }
    
    /**
     * Generates a random double within range
     * @param min minimum value (inclusive)
     * @param max maximum value (exclusive)
     * @return random double
     */
    public double generateRandomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    
    /**
     * Generates a random boolean
     * @return random boolean
     */
    public boolean generateRandomBoolean() {
        return random.nextBoolean();
    }
    
    // Date and Time Generation
    
    /**
     * Generates a random date within range
     * @param startDate start date
     * @param endDate end date
     * @return random date
     */
    public LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return LocalDate.ofEpochDay(randomDay);
    }
    
    /**
     * Generates a random date in the past
     * @param maxDaysAgo maximum days ago
     * @return random past date
     */
    public LocalDate generatePastDate(int maxDaysAgo) {
        LocalDate now = LocalDate.now();
        LocalDate pastDate = now.minusDays(maxDaysAgo);
        return generateRandomDate(pastDate, now);
    }
    
    /**
     * Generates a random date in the future
     * @param maxDaysAhead maximum days ahead
     * @return random future date
     */
    public LocalDate generateFutureDate(int maxDaysAhead) {
        LocalDate now = LocalDate.now();
        LocalDate futureDate = now.plusDays(maxDaysAhead);
        return generateRandomDate(now, futureDate);
    }
    
    /**
     * Generates a random date time
     * @param startDateTime start date time
     * @param endDateTime end date time
     * @return random date time
     */
    public LocalDateTime generateRandomDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long startSeconds = startDateTime.toLocalDate().toEpochDay() * 24 * 60 * 60 + 
                           startDateTime.toLocalTime().toSecondOfDay();
        long endSeconds = endDateTime.toLocalDate().toEpochDay() * 24 * 60 * 60 + 
                         endDateTime.toLocalTime().toSecondOfDay();
        
        long randomSeconds = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds + 1);
        long days = randomSeconds / (24 * 60 * 60);
        int secondsOfDay = (int) (randomSeconds % (24 * 60 * 60));
        
        return LocalDate.ofEpochDay(days).atTime(0, 0).plusSeconds(secondsOfDay);
    }
    
    // Text Generation
    
    /**
     * Generates random text
     * @param wordCount number of words
     * @return random text
     */
    public String generateText(int wordCount) {
        return faker.lorem().words(wordCount).toString().replaceAll("[\\[\\],]", "");
    }
    
    /**
     * Generates a random sentence
     * @return random sentence
     */
    public String generateSentence() {
        return faker.lorem().sentence();
    }
    
    /**
     * Generates random sentences
     * @param sentenceCount number of sentences
     * @return random sentences
     */
    public String generateSentences(int sentenceCount) {
        return faker.lorem().sentences(sentenceCount).toString().replaceAll("[\\[\\],]", "");
    }
    
    /**
     * Generates a random paragraph
     * @return random paragraph
     */
    public String generateParagraph() {
        return faker.lorem().paragraph();
    }
    
    // Collection Utilities
    
    /**
     * Selects a random element from array
     * @param array array to select from
     * @param <T> type parameter
     * @return random element
     */
    @SafeVarargs
    public final <T> T selectRandom(T... array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        return array[random.nextInt(array.length)];
    }
    
    /**
     * Selects a random element from list
     * @param list list to select from
     * @param <T> type parameter
     * @return random element
     */
    public <T> T selectRandom(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be empty");
        }
        return list.get(random.nextInt(list.size()));
    }
    
    /**
     * Shuffles a list and returns specified number of elements
     * @param list list to shuffle
     * @param count number of elements to return
     * @param <T> type parameter
     * @return shuffled sublist
     */
    public <T> List<T> selectRandomElements(List<T> list, int count) {
        if (count > list.size()) {
            throw new IllegalArgumentException("Count cannot be greater than list size");
        }
        
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, count);
    }
    
    // Data Validation Utilities
    
    /**
     * Validates email format
     * @param email email to validate
     * @return true if email format is valid
     */
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    /**
     * Validates phone number format (US format)
     * @param phoneNumber phone number to validate
     * @return true if phone number format is valid
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^(\\+1-?)?\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber).matches();
    }
    
    /**
     * Validates ZIP code format (US format)
     * @param zipCode ZIP code to validate
     * @return true if ZIP code format is valid
     */
    public boolean isValidZipCode(String zipCode) {
        String zipRegex = "^[0-9]{5}(?:-[0-9]{4})?$";
        Pattern pattern = Pattern.compile(zipRegex);
        return pattern.matcher(zipCode).matches();
    }
    
    /**
     * Validates credit card number using Luhn algorithm
     * @param creditCardNumber credit card number to validate
     * @return true if credit card number is valid
     */
    public boolean isValidCreditCardNumber(String creditCardNumber) {
        // Remove spaces and dashes
        String cleanNumber = creditCardNumber.replaceAll("[\\s-]", "");
        
        // Check if all characters are digits
        if (!cleanNumber.matches("\\d+")) {
            return false;
        }
        
        // Apply Luhn algorithm
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cleanNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cleanNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10) == 0;
    }
    
    /**
     * Validates password strength
     * @param password password to validate
     * @param minLength minimum required length
     * @param requireUppercase require uppercase letters
     * @param requireLowercase require lowercase letters
     * @param requireNumbers require numbers
     * @param requireSpecialChars require special characters
     * @return true if password meets requirements
     */
    public boolean isValidPassword(String password, int minLength, boolean requireUppercase,
                                 boolean requireLowercase, boolean requireNumbers,
                                 boolean requireSpecialChars) {
        if (password.length() < minLength) {
            return false;
        }
        
        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            return false;
        }
        
        if (requireLowercase && !password.matches(".*[a-z].*")) {
            return false;
        }
        
        if (requireNumbers && !password.matches(".*\\d.*")) {
            return false;
        }
        
        if (requireSpecialChars && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Formats date to string
     * @param date date to format
     * @param pattern date pattern
     * @return formatted date string
     */
    public String formatDate(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
    
    /**
     * Formats date time to string
     * @param dateTime date time to format
     * @param pattern date time pattern
     * @return formatted date time string
     */
    public String formatDateTime(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
    
    /**
     * Gets the underlying Faker instance for advanced usage
     * @return Faker instance
     */
    public Faker getFaker() {
        return faker;
    }
}