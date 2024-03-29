package hellocucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

class IsItFriday
{
    static String isItFriday(String today) {
        if( today.equals("Friday"))
            return "TGIF";

        if( today.equals("Thursday"))
            return "One more day!";

        return "Nope";
    }
}

public class StepDefinitions
{
    private String today;
    private String actualAnswer;

    @Given("today is {string}")
    public void today_is(String today) {
        this.today = today;
    }

    @When("I ask whether it's Friday yet")
    public void i_ask_whether_it_is_Friday_yet() {
        actualAnswer = IsItFriday.isItFriday(today);
    }

    @Then("I should be told {string}")
    public void i_should_be_told(String expectedAnswer ) {
        assertEquals(expectedAnswer, actualAnswer);
    }
}
