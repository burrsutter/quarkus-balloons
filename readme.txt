
1. Start Kafka broker

docker-compose up

2. Start the Game Server
cd balloon-game-Server
mvn compile quarkus:dev

3. Start the Pop Stream analyzer (determines achievements & bonuses)
cd  balloon-pop-tream
mvn compile quarkus:dev

4. Start the mobile app dev mode
cd balloon-game-mobile
npm start

5. Open Game in browser localhost:4200 

6. Start game
curl http://localhost:8080/142sjer43/start

7. Play Game
curl http://localhost:8080/142sjer43/play

8. Pause Game
curl http://localhost:8080/142sjer43/pause

9. Game Over
curl http://localhost:8080/142sjer43/gameover



