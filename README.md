# FoodJunkies
Food Discovery App

This app was developed to provide an answer to the age old question - "Where should I eat?". FoodJunkies is a food recommendation app that provides a more personalized experience for its users as they continue to use it.

# Login
Login credentials are stored on a MySQL database hosted by Amazon Web Services. Users create an account and can login from a different device and keep 

<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/Login.png" width="450" height="790">

# Cuisine Quiz
If it is the user's first time, upon registering they are brought to the Cuisine Quiz used to determine the user's initial cuisine preferences. The quiz is designed similar to Tinder's card swipes to introduce a more fun and interactive feature. Similarly, swiping left dislikes the cuisine and swiping right likes it.

<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/Tinder.png" width="450" height="790">

# Constraints
After the user completes the cuisine quiz they are brought to the Constraints settings page. There are 3 sliders which can be used to adjust the user's Budget, Time, and Distance constraints. These constraints are used to find the top restaurants within the area. The settings are saved locally onto an SQLite database.

<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/Constraints.png" width="450" height="790">

# Recommendations
Dish recommendations are based on an algorithm which determines how likely a dish from a cuisine will be recommended. The initial Cuisine Quiz will provide a base for the recommendations, and as the user continues to visit new restaurants and try new dishes the algorithm will allow for more personalized recommendations. The user has the option to roll for a new dish, or dislike them as well.

<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/Recommendations.png" width="450" height="790">

# Display Restaurant
When the user selects a dish, they will be shown the top 3 restaurants in the area that match the Budget, Time, and Distance constraints. Yelp's Fusion API is utilized to fetch a list of restaurants meeting the criteria, and is trimmed down to just 3 to reduce user's decision making.

<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/DisplayRestaurant.png" width="450" height="790">
<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/RestrauntDetails.png" width="450" height="790">


# Ratings
When users visit a restaurant serving the recommended dish, the user can then go back to it and rate both the dish and restaurant. The feedback is used to either stop recommending the dish and lower cuisine rating, or increase them if the user had a good experience.

<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/Ratings.png" width="450" height="790">

# Search
Sometimes people are just really craving something, there is a search feature that allows users to search by cuisines or foods if they're not up for trying something new.

<img src="https://github.com/kahhauyap/FoodJunkies/blob/master/screen%20shots/Search.png" width="450" height="790">





