package example.apilimittest.global.repository;


import example.apilimittest.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByID(Long id);
    User findByUId(String uid);
}
