package view;
import cellularData.Country;
import cellularData.DataModel;
import cellularData.LinkedList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * The GraphView Class
 * @author Matt W, Paul H, Evan R
 */
public class GraphView extends LineChart <Number,Number> {
    // Graph parameters
    private int numCountries = 8;
    final int WIDTH = 1000, HEIGHT = 600;

    // member fields
    private DataModel model;
    private NumberAxis xAxis, yAxis;
    private LinkedList<Country> selectedCountryList;

    /**
     * Constructor. Establishes minimum LineChart size and assigns DataModel
     * Country object array to a member field.
     * @param model contains Country object array for graphing.
     */
    public GraphView(DataModel model) {
        super(new NumberAxis(), new NumberAxis());

        this.setMinWidth(WIDTH);
        this.setMinHeight(HEIGHT);
        this.xAxis = (NumberAxis) getXAxis();
        this.yAxis = (NumberAxis) getYAxis();
        this.model = model;
    }

    /**
     * Returns series of subscription data versus year for a given Country object.
     * @param c Country to build subscription versus year series from.
     * @return specified series.
     */
    public Series<Number, Number> seriesFromCountry(Country c) {
        Series<Number, Number> countrySeries = new Series<>();
        int length = c.getEndYear() - c.getStartYear();
        for (int i = 0; i < length; i++){
            int tempYear = c.getStartYear()+i;
            double tempData = c.getNumSubscriptionsForPeriod(tempYear,tempYear);
            countrySeries.getData().add(new Data<>(tempYear, tempData));
        }
        return countrySeries;
    }

    /**
     * Finds out how many Countries to display, by getting keyboard input;
     * Builds linked list of random country objects, by calling selectedCountryList;
     * Traverses the list using iterator;
     * Converts each list object to a Series object, by calling seriesFromCountry;
     * Then adds each Series object to the ObservableList.
     */
     public void update()
     {
         int requestedSize = numCountries;
         CountrySelector selectedCountryArray = new CountrySelector(model.getCellularData(), requestedSize);
         selectedCountryList = selectedCountryArray.selectCountries();

         // label axes and set range of X-axis
         Country aCountry = selectedCountryList.getIndex(0);
         xAxis.setLabel("Year");
         xAxis.setAutoRanging(false);
         xAxis.setUpperBound(aCountry.getEndYear());
         xAxis.setLowerBound(aCountry.getStartYear());
         yAxis.setLabel(model.getTitle());

         for (Country currentCountry :selectedCountryList)
         {
             Series<Number,Number> pointsOnGraph = seriesFromCountry(currentCountry);
             pointsOnGraph.setName(currentCountry.getName());
             getData().add(pointsOnGraph);
         }
     }

    /**
     * Returns Linked List of countries that have been randomly chosen.
     * @return specified Linked List.
     */
     public LinkedList<Country> getSelectedCountryList() { return selectedCountryList; }

    /**
     * Returns number of countries to randomly select.
     * @return specified number.
     */
    public int getNumCountries() { return numCountries; }

    /**
     * Sets number of countries to randomly select.
     * @param numberOfCountries the number of countries to randomly select.
     */
    public void setNumCountries(int numberOfCountries) { numCountries = numberOfCountries; }
}