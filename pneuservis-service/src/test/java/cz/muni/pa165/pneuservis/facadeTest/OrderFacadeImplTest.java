package cz.muni.pa165.pneuservis.facadeTest;

import cz.fi.muni.pa165.pneuservis.dto.CreateOrderDTO;
import cz.fi.muni.pa165.pneuservis.dto.OrderDTO;
import cz.fi.muni.pa165.pneuservis.dto.ServiceDTO;
import cz.fi.muni.pa165.pneuservis.entity.Order;
import cz.fi.muni.pa165.pneuservis.entity.Service;
import cz.fi.muni.pa165.pneuservis.enums.PaymentType;
import cz.fi.muni.pa165.pneuservis.facade.OrderFacade;
import cz.fi.muni.pa165.pneuservis.service.configuration.ServiceConfiguration;
import cz.fi.muni.pa165.pneuservis.service.facade.OrderFacadeImpl;
import cz.fi.muni.pa165.pneuservis.service.services.BeanMappingService;
import cz.fi.muni.pa165.pneuservis.service.services.BeanMappingServiceImpl;
import cz.fi.muni.pa165.pneuservis.service.services.OrderService;
import org.hibernate.service.spi.ServiceException;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by vit.holasek on 25.11.2016.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class OrderFacadeImplTest {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderFacade orderFacade = new OrderFacadeImpl();

    @Spy
    @Autowired
    private BeanMappingService beanMappingService = new BeanMappingServiceImpl();

    private Order order1;
    private Order order2;
    private CreateOrderDTO newOrderDTO;
    private ServiceDTO serviceDTO;
    private Service service1;
    private List<Order> allOrders;
    private List<Order> clientOrders;

    @BeforeClass
    public void setup() throws ServiceException {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void prepareTest() throws ServiceException {
        service1 = new Service();
        service1.setId(1L);
        service1.setTypeOfCar("Audi");
        service1.setDuration(10);
        service1.setPrice(new BigDecimal(79));
        service1.setDescription("Test");
        service1.setNameOfService("Tire change");
        service1.setOwnParts(false);

        order1 = new Order();
        order1.setId(1L);
        order1.setPaymentConfirmed(true);
        order1.setShipped(true);
        order1.setPaymentType(PaymentType.COD);
        order1.setClientId(1L);
        order1.setNote("Test");
        order1.getListOfServices().add(service1);

        order2 = new Order();
        order2.setId(2L);
        order2.setPaymentConfirmed(false);
        order2.setShipped(false);
        order2.setPaymentType(PaymentType.CARD);
        order2.setClientId(2L);
        order2.setNote("Test1");
        order2.getListOfServices().add(service1);

        serviceDTO = new ServiceDTO();
        serviceDTO.setId(1L);
        serviceDTO.setDuration(5);
        serviceDTO.setNameOfService("Change of gear");
        serviceDTO.setOwnParts(false);
        serviceDTO.setTypeOfCar("Honda");
        serviceDTO.setDescription("Test");
        serviceDTO.setPrice(BigDecimal.valueOf(1400));

        newOrderDTO = new CreateOrderDTO();
        newOrderDTO.setPaymentType(PaymentType.CARD);
        newOrderDTO.setClientId(2L);
        newOrderDTO.setNote("Test1");
        newOrderDTO.setListOfServices(new ArrayList<>());
        newOrderDTO.setListOfTires(new ArrayList<>());
        newOrderDTO.getListOfServices().add(serviceDTO);

        allOrders = new ArrayList<>();
        allOrders.add(order1);
        allOrders.add(order2);

        clientOrders = new ArrayList<>();
        clientOrders.add(order1);

        when(orderService.create(order1)).thenAnswer(invoke -> {
            Order order = invoke.getArgumentAt(0, Order.class);
            order.setId(1L);
            return order;
        });
    }

    @Captor ArgumentCaptor<Order> captor;
    @Test
    public void createTest() {
        OrderDTO orderDTO = orderFacade.create(newOrderDTO);
        verify(orderService).create(captor.capture());
        Assert.assertEquals(orderDTO.getId(), new Long(1));
        Assert.assertNotNull(captor.getValue());
        Order capturedOrder = captor.getValue();
        Assert.assertEquals(capturedOrder.getPaymentType(), PaymentType.COD);
        Assert.assertEquals(capturedOrder.getClientId(), newOrderDTO.getClientId());
        Assert.assertNotNull(capturedOrder.getListOfServices());
        Assert.assertEquals(capturedOrder.getListOfServices().size(), newOrderDTO.getListOfServices().size());
    }
}