package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.BookingConfirmationTO;
import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.UserDataTO;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 20.06.15.
 *
 * This is a real hacking service that does booking of appointments based on
 * search results provided by AmtService
 */
@Service
public class BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);
    private static final String BOOKING_BASE = "https://service.berlin.de/terminvereinbarung/termin/";
    private static final String CONFIRM_URL = "https://service.berlin.de/terminvereinbarung/termin/bestaetigung.php";

    public BookingConfirmationTO performBooking (PossibleBookingTO possibleBooking,UserDataTO user) {
        BookingConfirmationTO result = new BookingConfirmationTO ();
        Connection.Response bookingForm = null;
        try {
            final WebClient webClient = new WebClient();

            ConfirmHandler confirmHandler = new ConfirmHandler() {
                @Override
                public boolean handleConfirm(Page page, String s) {
                    return true;
                }
            };
            webClient.setConfirmHandler(confirmHandler);

            // Since we aren't actually manipulating the page, we don't assign
            // it to a variable - it's enough to know that it loaded.
            final HtmlPage page1 = webClient.getPage(BOOKING_BASE + possibleBooking.getBookingUrl());

            // Get the form that we are dealing with and within that form,
            // find the submit button and the field that we want to change.
            HtmlForm toFill = null;
            List<HtmlForm> forms = page1.getForms();
            for (HtmlForm form : forms) {
                if (form.getAttribute("class").contains("form-land form-zms")) {
                    toFill = form;
                }
            }

            final HtmlTextInput textField = toFill.getInputByName("Nachname");

            // Change the value of the text field
            textField.click();
            textField.setValueAttribute(user.getFirstName() + " " + user.getLastName());
//            textField.setText(user.getFirstName() + " " + user.getLastName());

            final HtmlTextInput textField2 = toFill.getInputByName("EMail");
            textField2.click();
            textField2.setValueAttribute(user.getEmail());

            HtmlTextInput textField3 = toFill.getInputByName("telefonnummer_fuer_rueckfragen");
            textField3.click();
            textField3.setValueAttribute(user.getPhone());
//            textField2.setText(user.getEmail());

            List<HtmlInput> buttons = toFill.getInputsByValue("Termin eintragen");
            HtmlInput submit = null;
            for (HtmlInput button : buttons) {
                if (button.getAttribute("disabled") == null || !button.getAttribute("disabled").equals("disabled")) {
                    submit = button;
                }
            }
            // Now submit the form by clicking the button and get back the second page.
            final HtmlPage page2 = submit.click();
            Element addressData = Jsoup.parse(page2.asXml()).select(".box-gray-light.add-paperclip.amt").select(".row-fluid").get(2);
            Element mainData = Jsoup.parse(page2.asXml()).select(".box-gray-light.add-paperclip.amt").select(".row-fluid").get(0);
            Elements selection = mainData.select(".block-item span");
            result.setName(selection.get(0).html());
            result.setEmail(selection.get(1).html());
            result.setPhone(selection.get(2).html());
            result.setCancelCode(selection.get(3).html());
            result.setNumber(mainData.select(".block-item div").html());
            result.setDate(addressData.select(".block-item div").get(2).html());
            result.setAddress(addressData.select(".block-item div").get(1).html());
            result.setPlace(addressData.select(".block-item div").get(0).html());
//            bookingForm = Jsoup.connect(BOOKING_BASE + possibleBooking.getBookingUrl())
//                    .method(Connection.Method.GET)
//                    .execute();
//            Document bookingPage = bookingForm.parse();
////            Nachname:Omer Oman
////            EMail:g5608772@trbvm.com
////            Anmerkung:
////            Datum:2015-07-20
////            Uhrzeit:12:45:00
////              g5615338@trbvm.com
////            SID:560
////            OID:33230
////            OIDListe:33230,27922
////            buergerID:122057
////            slots:1
////            herkunft:http://service.berlin.de/dienstleistung/326423/
////            anliegen[]:326423
////            emailpruefen:1
////            gelesen:1
//            Document document = Jsoup.connect(CONFIRM_URL)
//                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.125 Safari/537.36")
//                    .referrer(BOOKING_BASE + possibleBooking.getBookingUrl())
//                    .data("Nachname", user.getFirstName() + " " + user.getLastName())
//                    .data("EMail", user.getEmail())
//                    .data("Datum", possibleBooking.getDate())
//                    .data("Uhrzeit", possibleBooking.getBookingTime())
//                    .data("SID", getSid(bookingPage))
//                    .data("OID", getOid(bookingPage))
//                    .data("OIDListe", getOids(bookingPage))
//                    .data("buergerID", getBuergerID(bookingPage))
//                    .data("slots", getSlots(bookingPage))
//                    .data("herkunft", getOrigin(bookingPage))
//                    .data("anliegen", getAnliegen(bookingPage))
//                    .data("emailpruefen", "1")
//                    .data("gelesen","1")
//                    .cookies(bookingForm.cookies()).cookie("POPUPCHECK","1434887722494")
//                    .post();
//            System.out.println(document);
        } catch (IOException e) {
            LOGGER.error("cant open booking page");
        }

        return result;
    }

    private String getAnliegen(Document bookingPage) {
        return bookingPage.select("input[name=anliegen[]]").attr("value");
    }

    private String getOrigin(Document bookingPage) {
        return bookingPage.select("input[name=herkunft]").attr("value");
    }

    private String getSlots(Document bookingPage) {
        return bookingPage.select("input[name=slots]").attr("value");
    }

    private String getBuergerID(Document bookingPage) {
        return bookingPage.select("input[name=buergerID]").attr("value");
    }

    private String getOids(Document bookingPage) {
        return bookingPage.select("input[name=OIDListe]").attr("value");
    }

    private String getOid(Document bookingPage) {
        return bookingPage.select("input[name=OID]").attr("value");
    }

    private String getSid(Document bookingForm) {
        return bookingForm.select("input[name=SID]").attr("value");
    }
}
