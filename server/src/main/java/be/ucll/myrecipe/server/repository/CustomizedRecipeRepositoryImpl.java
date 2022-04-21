package be.ucll.myrecipe.server.repository;

import be.ucll.myrecipe.server.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomizedRecipeRepositoryImpl implements CustomizedRecipeRepository {

    private final EntityManager em;

    public CustomizedRecipeRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public long countByUserLoginAndNameAndRating(String userLogin, String name, Integer rating) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var root = cq.from(Recipe.class);

        cq.select(cb.count(root));
        cq.where(toPredicates(cb, root, userLogin, name, rating).toArray(Predicate[]::new));

        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public Page<Recipe> findAllByUserLoginAndNameAndRating(String userLogin, String name, Integer rating, Pageable pageable) {
        var elements = countByUserLoginAndNameAndRating(userLogin, name, rating);

        if (elements == 0) {
            return Page.empty(pageable);
        }

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Recipe.class);
        var root = cq.from(Recipe.class);

        cq.where(toPredicates(cb, root, userLogin, name, rating).toArray(Predicate[]::new));
        cq.orderBy(toOrders(pageable.getSort(), root, cb));

        var query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return new PageImpl<>(query.getResultList(), pageable, elements);
    }

    private List<Predicate> toPredicates(CriteriaBuilder cb, From<?, ?> from, String userLogin, String name, Integer rating) {
        var predicates = new ArrayList<Predicate>();
        if (userLogin != null && !userLogin.isBlank()) {
            predicates.add(cb.equal(from.get("user").get("login"), userLogin));
        }
        if (name != null && !name.isBlank()) {
            predicates.add(cb.like(cb.lower(from.get("name")), name.toLowerCase() + "%"));
        }
        if (rating != null) {
            predicates.add(cb.greaterThanOrEqualTo(from.get("rating"), rating));
        }
        return predicates;
    }

    public List<Order> toOrders(Sort sort, From<?, ?> from, CriteriaBuilder cb) {
        if (sort.isUnsorted()) {
            return Collections.emptyList();
        }
        return sort
                .stream()
                .map(order -> toJpaOrder(order, from, cb))
                .collect(Collectors.toList());
    }

    private javax.persistence.criteria.Order toJpaOrder(Sort.Order order, From<?, ?> from, CriteriaBuilder cb) {
        var expression = from.get(order.getProperty());
        return order.isAscending() ? cb.asc(expression) : cb.desc(expression);
    }
}
