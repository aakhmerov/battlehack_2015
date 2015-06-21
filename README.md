# battlehack_2015
Solution for battlehack 2015 - Berlin

Command to trigger services fetching for the user
curl -H "Content-Type: application/json" -X POST -d '{"email":null,"lastName":null,"firstName":null,"phone":null,"time":"10,18","zipCodes":null,"days":["monday","tuesday","wednesday","thursday","friday","saturday"],"serviceId":"120686"}' https://aqueous-meadow-5830.herokuapp.com/melder-api/services/amt/bookings/fetch

Command to get bookings that are already fetched for the user
curl -H "Content-Type: application/json" -X POST -d '{"id":3,"email":null,"lastName":null,"firstName":null,"phone":null,"time":"10,18","zipCodes":null,"days":["monday","tuesday","wednesday","thursday","friday","saturday"],"serviceId":"120686"}' https://aqueous-meadow-5830.herokuapp.com/melder-api/services/amt/bookings

Command to test that cache is valid for 5 minutes
curl -H "Content-Type: application/json" -X POST -d '{"id":3,"email":null,"lastName":null,"firstName":null,"phone":null,"time":"10,18","zipCodes":null,"days":["monday","tuesday","wednesday","thursday","friday","saturday"],"serviceId":"120686"}' https://aqueous-meadow-5830.herokuapp.com/melder-api/services/amt/bookings/fetch