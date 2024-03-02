import pygame
from pygame.locals import *
import sys
import random

pygame.init()
pygame.mixer.init()

WIDTH, HEIGHT = 800, 600
SCREEN = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Super Mario Bros")

WHITE = (255, 255, 255)
GREEN = (0, 255, 0)
RED = (255, 0, 0)
YELLOW = (255, 255, 0)

clock = pygame.time.Clock()

class Player(pygame.sprite.Sprite):
    def __init__(self):
        super().__init__()
        self.image = pygame.Surface((50, 50))
        self.image.fill(GREEN)
        self.rect = self.image.get_rect()
        self.rect.center = (WIDTH / 2, HEIGHT - 30)
        self.speed = 5

    def update(self):
        keys = pygame.key.get_pressed()
        if keys[K_LEFT]:
            self.rect.x -= self.speed
        if keys[K_RIGHT]:
            self.rect.x += self.speed
        if keys[K_UP]:
            self.rect.y -= self.speed
        if keys[K_DOWN]:
            self.rect.y += self.speed
        self.rect.clamp_ip(SCREEN.get_rect())  # Keep player inside screen

class Enemy(pygame.sprite.Sprite):
    def __init__(self):
        super().__init__()
        self.image = pygame.Surface((50, 50))
        self.image.fill(RED)
        self.rect = self.image.get_rect()
        self.rect.center = (WIDTH / 2, 30)
        self.speed = 5

    def update(self):
        self.rect.x -= self.speed
        if self.rect.right < 0:
            self.rect.left = WIDTH
            self.rect.top = random.randint(30, HEIGHT - 30)

class Projectile(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface((10, 10))
        self.image.fill(YELLOW)
        self.rect = self.image.get_rect()
        self.rect.center = (x, y)
        self.speed = 10

    def update(self):
        self.rect.y -= self.speed
        if self.rect.bottom < 0:
            self.kill()

player = Player()
player_group = pygame.sprite.GroupSingle(player)

enemy_group = pygame.sprite.Group()
for _ in range(3):  # Create 3 enemies
    enemy = Enemy()
    enemy_group.add(enemy)

projectile_group = pygame.sprite.Group()

score = 0
font = pygame.font.SysFont(None, 36)

# Game loop
running = True
while running:
    for event in pygame.event.get():
        if event.type == QUIT:
            running = False
        elif event.type == KEYDOWN:
            if event.key == K_SPACE and len(projectile_group) < 3:  # Limit projectiles on screen
                projectile = Projectile(player.rect.centerx, player.rect.centery)
                projectile_group.add(projectile)

    # Check for collisions
    hits = pygame.sprite.groupcollide(enemy_group, projectile_group, True, True)
    for hit in hits:
        score += 1
        enemy = Enemy()
        enemy_group.add(enemy)

    # Check for player-enemy collision
    if pygame.sprite.spritecollide(player, enemy_group, False):
        running = False

    # Update sprites
    player_group.update()
    enemy_group.update()
    projectile_group.update()

    # Draw everything
    SCREEN.fill(WHITE)
    player_group.draw(SCREEN)
    enemy_group.draw(SCREEN)
    projectile_group.draw(SCREEN)

    # Draw score
    score_text = font.render("Score: " + str(score), True, BLACK)
    SCREEN.blit(score_text, (10, 10))

    pygame.display.flip()
    clock.tick(30)  # Limit frame rate to 30 FPS

pygame.quit()
sys.exit()
