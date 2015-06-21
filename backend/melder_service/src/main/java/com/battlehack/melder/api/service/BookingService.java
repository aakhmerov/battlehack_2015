package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.UserDataTO;
import com.battlehack.melder.api.tos.appointment.AppointmentRequestTO;
import com.battlehack.melder.api.tos.appointment.BookingConfirmationTO;
import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    /**
     * Go to website and perform booking for the user
     *
     * NOTE: This is working implementation and you will get registered for real
     * with all consequences, please use with caution!
     *
     * @param possibleBooking
     * @param user
     * @return
     */
    private BookingConfirmationTO performBooking (PossibleBookingTO possibleBooking,UserDataTO user) {
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
            result.setService(possibleBooking.getServiceId());
        } catch (IOException e) {
            LOGGER.error("cant open booking page");
        }

        return result;
    }

    /**
     * Wrapper around performing real booking of appointment for user.
     * In pilot version performs stubbing of data
     * TODO: uncomment invocation of real methods to feel the FORCE
     * @param requestTO
     * @return
     */
    public BookingConfirmationTO performBooking(AppointmentRequestTO requestTO) {
        BookingConfirmationTO result = stubResult(requestTO);

        return result;
    }

    /**
     *
     * @param requestTO
     * @return
     */
    private BookingConfirmationTO stubResult(AppointmentRequestTO requestTO) {
        BookingConfirmationTO result = new BookingConfirmationTO();
        result.setAddress(requestTO.getDesiredBooking().getPlaceAddress());
        result.setService(requestTO.getDesiredBooking().getServiceId());
        result.setName(requestTO.getUser().getFirstName() + " " + requestTO.getUser().getLastName());
        result.setPlace(requestTO.getDesiredBooking().getPlaceName());
        result.setCancelCode("3bef");
        result.setDate(requestTO.getDesiredBooking().getDate());
        result.setNumber("53579");
        result.setPhone(requestTO.getUser().getPhone());
        result.setEmail(requestTO.getUser().getEmail());
        result.setService(requestTO.getDesiredBooking().getServiceId());
        result.setUserId(requestTO.getUser().getId());
        result.setTime(requestTO.getDesiredBooking().getBookingTime());
        return result;
    }
}
