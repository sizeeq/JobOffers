package pl.joboffers;

public final class OfferResponseStubJson {

    private OfferResponseStubJson() {}

    public static String bodyWithZeroOffers() {
        return "[]".trim();
    }

    public static String bodyWithOneOffer() {
        return """
                [
                  {
                    "id": "1",
                    "company": "Acme Corp",
                    "title": "Junior Java Developer",
                    "salary": "7000–9000 PLN",
                    "offerUrl": "https://example.com/offers/1"
                  }
                ]
                """.trim();
    }

    public static String bodyWithTwoOffers() {
        return """
                [
                  {
                    "id": "1",
                    "company": "Acme Corp",
                    "title": "Junior Java Developer",
                    "salary": "7000–9000 PLN",
                    "offerUrl": "https://example.com/offers/1"
                  },
                  {
                    "id": "2",
                    "company": "SoftTech Solutions",
                    "title": "Java Developer Trainee",
                    "salary": "6000–8000 PLN",
                    "offerUrl": "https://example.com/offers/2"
                  }
                ]
                """.trim();
    }

    public static String bodyWithThreeOffers() {
        return """
                [
                  {
                    "id": "1",
                    "company": "Acme Corp",
                    "title": "Junior Java Developer",
                    "salary": "7000–9000 PLN",
                    "offerUrl": "https://example.com/offers/1"
                  },
                  {
                    "id": "2",
                    "company": "SoftTech Solutions",
                    "title": "Java Developer Trainee",
                    "salary": "6000–8000 PLN",
                    "offerUrl": "https://example.com/offers/2"
                  },
                  {
                    "id": "3",
                    "company": "NextGen Software",
                    "title": "Backend Java Intern",
                    "salary": "4500–6500 PLN",
                    "offerUrl": "https://example.com/offers/3"
                  }
                ]
                """.trim();
    }

    public static String bodyWithFourOffers() {
        return """
                [
                  {
                    "id": "1",
                    "company": "Acme Corp",
                    "title": "Junior Java Developer",
                    "salary": "7000–9000 PLN",
                    "offerUrl": "https://example.com/offers/1"
                  },
                  {
                    "id": "2",
                    "company": "SoftTech Solutions",
                    "title": "Java Developer Trainee",
                    "salary": "6000–8000 PLN",
                    "offerUrl": "https://example.com/offers/2"
                  },
                  {
                    "id": "3",
                    "company": "NextGen Software",
                    "title": "Backend Java Intern",
                    "salary": "4500–6500 PLN",
                    "offerUrl": "https://example.com/offers/3"
                  },
                  {
                    "id": "4",
                    "company": "CloudForge",
                    "title": "Junior Backend Engineer",
                    "salary": "7500–9500 PLN",
                    "offerUrl": "https://example.com/offers/4"
                  }
                ]
                """.trim();
    }
}
