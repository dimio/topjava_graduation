package ru.javawebinar.topjava.graduation.web.restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.javawebinar.topjava.graduation.RestaurantTestData;
import ru.javawebinar.topjava.graduation.model.Menu;
import ru.javawebinar.topjava.graduation.model.Restaurant;
import ru.javawebinar.topjava.graduation.service.RestaurantService;
import ru.javawebinar.topjava.graduation.web.AbstractControllerTest;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.graduation.MenuTestData.*;
import static ru.javawebinar.topjava.graduation.RestaurantTestData.*;
import static ru.javawebinar.topjava.graduation.TestUtil.readFromJson;
import static ru.javawebinar.topjava.graduation.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.graduation.UserTestData.ADMIN;
import static ru.javawebinar.topjava.graduation.web.json.JsonUtil.writeAdditionProps;
import static ru.javawebinar.topjava.graduation.web.json.JsonUtil.writeValue;
import static ru.javawebinar.topjava.graduation.web.restaurant.AdminRestaurantController.REST_URL;

public class AdminRestaurantControllerTest extends AbstractControllerTest {

    @Autowired
    protected RestaurantService restaurantService;

    @Test
    void testGetUnauth() throws Exception {
        mockMvc.perform(get(REST_URL))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllMenusForRestaurant() throws Exception {
        mockMvc.perform(get(REST_URL + "/{restaurantId}/menus", RESTAURANT_1_ID)
            .with(userHttpBasic(ADMIN)))
            .andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(contentJson(MENU_R1_D29, MENU_R1_D28, MENU_R1_D27));
    }

    @Test
    void addRestaurant() throws Exception {
        Restaurant expected = new Restaurant(null, "New restaurant", null);
        ResultActions action = mockMvc.perform(post(REST_URL)
            .with(userHttpBasic(ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(writeValue(expected)))
            .andExpect(status().isCreated());

        Restaurant returned = readFromJson(action, Restaurant.class);
        expected.setId(returned.getId());

        RestaurantTestData.assertMatchRestaurant(returned, expected);
        RestaurantTestData.assertMatchRestaurant(restaurantService.getAll(Sort.by("id")), RESTAURANT_1, RESTAURANT_2, expected);
    }

    @Test
    void updateRestaurant() throws Exception {
        Restaurant updated = new Restaurant(RESTAURANT_1);
        updated.setName("UpdatedName");
        updated.setLunchMenus(RESTAURANT_2_MENUS);

        mockMvc.perform(put(REST_URL + "/{restaurantId}", RESTAURANT_1_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .with(userHttpBasic(ADMIN))
            .content(writeValue(updated)))
            .andDo(print())
            .andExpect(status().isNoContent());

        RestaurantTestData.assertMatchRestaurant(restaurantService.get(RESTAURANT_1_ID), updated);
    }

    @Test
    void deleteRestaurant() throws Exception {
        mockMvc.perform(delete(REST_URL + "/{restaurantId}", RESTAURANT_1_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .with(userHttpBasic(ADMIN)))
            .andDo(print())
            .andExpect(status().isNoContent());

        RestaurantTestData.assertMatchRestaurant(restaurantService.getAll(Sort.unsorted()), RESTAURANT_2);
    }

    @Test
    void addMenu() throws Exception {
        Menu expected = new Menu(null, null, "new dishes", 10, 99);

        ResultActions action = mockMvc.perform(post(REST_URL + "/{restaurantId}/menus", RESTAURANT_1_ID)
            .with(userHttpBasic(ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(writeValue(expected)))
            .andExpect(status().isCreated());

        Menu returned = readFromJson(action, Menu.class);
        expected.setId(returned.getId());
        expected.setActual(returned.getActual());

        assertMatch(returned, expected);
        assertMatch(restaurantService.getAllMenusForRestaurant(RESTAURANT_1_ID), expected, MENU_R1_D29, MENU_R1_D28, MENU_R1_D27);
    }

    @Test
    void updateMenu() throws Exception {
        Menu updated = new Menu(MENU_R1_D27);
        LocalDate actual = LocalDate.now();
        updated.setDishes("Updated Dishes");
        updated.setActual(actual);
        updated.setPriceInt(9999);

        mockMvc.perform(put(REST_URL + "/{restaurantId}/menus/{menuId}", RESTAURANT_1_ID, MENU_R1_D27.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .with(userHttpBasic(ADMIN))
            .content(writeAdditionProps(updated, Map.of("restaurant", RESTAURANT_1, "actual", actual))))
            .andDo(print())
            .andExpect(status().isNoContent());

        assertMatch(restaurantService.getMenuForRestaurant(RESTAURANT_1_ID, MENU_R1_D27.getId()), updated);
    }

    @Test
    void deleteMenu() throws Exception {
        mockMvc.perform(delete(REST_URL + "/{restaurantId}/menus/{menuId}", RESTAURANT_1_ID, MENU_R1_D27.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .with(userHttpBasic(ADMIN)))
            .andDo(print())
            .andExpect(status().isNoContent());

        assertMatch(restaurantService.getAllMenusForRestaurant(RESTAURANT_1_ID), MENU_R1_D29, MENU_R1_D28);
    }
}
